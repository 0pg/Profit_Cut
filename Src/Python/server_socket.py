from socket import *
from threading import *
import json

class server_socket(object):
	def __init__(self):
		self.tcp_sock = socket(AF_INET, SOCK_STREAM)
		self.udp_sock = socket(AF_INET, SOCK_DGRAM)
		self.addr = gethostbyname(gethostname())
		self.transac = []
		self.blocks = []
	
	'''
		udp socket의 주소와 포트 고정
	'''
	def udp_bind(self):
		self.udp_sock.bind(('', 12222))

	'''
		client로부터 요청이 들어오면 검증 후 쓰레드를 생성해 데이터 전송
		:param
		subject : 요청하는 데이터를 구별하기 위한 문자열
		chain_info : 체인과 관련된 데이터
		transaction_info : transaction 데이터
	'''
	def udp_server(self, subject, chain_info):
		while True:
			data, c_addr = self.udp_sock.recvfrom(100)
			data = data.decode()
			if "Profit_Cut?"+subject in data:
				self.udp_sock.sendto("Profit_OK".encode(), c_addr)
				t = Thread(target = self.tcp_send_data, args = (chain_info,))
				t.start()
			elif "Profit_Cut_transac"+subject in data:
				data = data.split('//')
				transaction = json.loads(data[1])
				t = Thread(target = self.recv_transac, args = (transaction,))
				t.start()
			elif "Profit_Cut_block"+subject in data:
				data = data.split('//')
				block = json.loads(data[1])
				t = Thread(target = recv_block, args = (block,))
				t.start()
	'''
		새로 전파된 trasaction을 리스트에 저장
		:param
		trasaction : 새로 전파된 transaction
	'''			
	def recv_transac(self, transaction):
		self.transac.append(transaction)

	'''
		새로 전파된 block을 리스트에 저장
		:param
		block : 새로 전파된 block
	'''
	def recv_block(self, block):
		self.blocks.append(block)


	'''
		tcp socket의 주소와 포트 고정
	'''
	def tcp_bind(self):
		self.tcp_sock.bind(('', 12223))
		self.tcp_sock.listen(0)
	
	'''
		client로부터 요청이 들어오면 데이터 전송
	'''
	def tcp_send_data(self, info):	
		client_sock, addr = self.tcp_sock.accept()
		client_sock.send(json.dumps(info))		


	'''
		소켓 종료
	'''
	def disconnect(self, sock):
		sock.close()

sock = server_socket()
sock.udp_bind()
sock.tcp_bind()
info = [["chain"], ["checking list"]]
sock.udp_server('vote', info)
