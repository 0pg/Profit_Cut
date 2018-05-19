from socket import *
from threading import *

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
		tcp socket server에 데이터 전송할 것을 요청 
		:param
		addrlist : tcp server 들의 주소
		chainlist : tcp server 로부터 받아온 데이터를 저장할 리스트
	'''
	def tcp_recv(self, addrlist, chainlist):
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
while len(addrlist) < 2: 
	addr = sock.broadcast_verify('vote')
	print(addr[0])
	addrlist.add(addr[0])

sock.tcp_recv(addrlist, chainlist)
print(chainlist)
