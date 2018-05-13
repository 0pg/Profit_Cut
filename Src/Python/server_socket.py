from socket import *
from threading import *
import json

class server_socket(object):
	def __init__(self):
		self.tcp_sock = socket(AF_INET, SOCK_STREAM)
		self.udp_sock = socket(AF_INET, SOCK_DGRAM)
		self.addr = gethostbyname(gethostname())
		self.chain = ['this', 'is', 'chain']

	def udp_bind(self):
		self.udp_sock.bind(('', 12222))
	
	def udp_server(self):
		while True:
			data, c_addr = self.udp_sock.recvfrom(100)
			data = data.decode()
			if "Profit_Cut?" in data:
				self.udp_sock.sendto("Profit_OK".encode(), c_addr)
				t = Thread(target = self.tcp_send_chain)
				t.start()

	def tcp_bind(self):
		self.tcp_sock.bind(('', 12223))
		self.tcp_sock.listen(8)
		
	def tcp_send_chain(self):	
		client_sock, addr = self.tcp_sock.accept()
		client_sock.send(json.dumps(self.chain).encode())		


	def disconnect(self, sock):
		sock.close()

sock = client_socket()
sock.udp_bind()
sock.tcp_bind()
sock.udp_server()
