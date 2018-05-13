from socket import *
from threading import *
import json

class server_socket(object):
	def __init__(self):
		self.tcp_sock = socket(AF_INET, SOCK_STREAM)
		self.udp_sock = socket(AF_INET, SOCK_DGRAM)
		self.addr = gethostbyname(gethostname())

	def udp_bind(self):
		self.udp_sock.bind(('', 12222))
	
	def udp_server(self, subject, info):
		while True:
			data, c_addr = self.udp_sock.recvfrom(100)
			data = data.decode()
			if "Profit_Cut?"+subject in data:
				self.udp_sock.sendto("Profit_OK".encode(), c_addr)
				t = Thread(target = self.tcp_send_chain, args = (info,))
				t.start()

	def tcp_bind(self):
		self.tcp_sock.bind(('', 12223))
		self.tcp_sock.listen(8)
		
	def tcp_send_chain(self, info):	
		client_sock, addr = self.tcp_sock.accept()
		client_sock.send(json.dumps(info).encode())		


	def disconnect(self, sock):
		sock.close()

sock = server_socket()
sock.udp_bind()
sock.tcp_bind()
info = [["chain"], ["checking list"]]
sock.udp_server(info)
