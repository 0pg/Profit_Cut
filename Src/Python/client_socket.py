from socket import *
from threading import *
import json

class client_socket(object):
	def __init__(self):
		self.tcp_sock = socket(AF_INET, SOCK_STREAM)
		self.udp_sock = socket(AF_INET, SOCK_DGRAM)
		self.udp_sock.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
		self.addr = gethostbyname(gethostname())

	'''
		udp socket server를 찾기 위한 broadcast
		:param
		subject : server로부터 원하는 데이터 요청하기 위한 문자열 
	'''
	def broadcast_verify(self, subject):
		self.udp_sock.sendto(("Profit_Cut?"+subject).encode(), ('255.255.255.255', 12222))
		data, addr = self.udp_sock.recvfrom(200)
		if data.decode() == "Profit_OK":
			return addr
		else:
			return False
	'''
		새로운 transaction을 broadcast
		:param
		subject : 목적지 chain의 식별자
		transacion : 전파하려는 새로운 transaction
	'''
	def broadcast_transac(self, subject, transaction):
		self.udp_sock.sendto(("Profit_Cut_transac"+subject+'//'+transaction).encode(), ('255.255.255.255', 12222))
		
	'''
		새로 생성된 block을 broadcast
		:param
		subject : 목적지 chain의 식별자
		block : 전파하려는 새로 생성된 block
	'''
	def broadcast_block(self, subject, block):
		self.udp_sock.sendto(("Profit_Cut_block"+subject+"//"+transaction).encode(), ('255.255.255.255', 12222))

	'''
		tcp socket server에 데이터 전송할 것을 요청 
		:param
		addrlist : tcp server 들의 주소
		chainlist : tcp server 로부터 받아온 데이터를 저장할 리스트
	'''
	def tcp_recv_chain(self, addrlist, chainlist):
		for addr in addrlist:
			try:
				self.tcp_sock.connect((addr, 12223))
				chain = self.tcp_sock.recv(65535)
				chainlist.append(chain)
			finally:
				self.tcp_sock.close()
				self.tcp_sock = socket(AF_INET, SOCK_STREAM)

sock = client_socket()
addrlist = set()
chainlist = []
transaction_info = {
			'voter' : '1',
			'choosen candidate' : 'A',
			}
while len(addrlist) < 2: 
	addr = sock.broadcast_verify('vote')
	print(addr[0])
	addrlist.add(addr[0])

sock.tcp_recv_chain(addrlist, chainlist)
print(chainlist)
sock.broadcast_transac('vote', json.dumps(transaction_info))
