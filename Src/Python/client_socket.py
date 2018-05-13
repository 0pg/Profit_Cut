from socket import *
from threading import *
import json

class server_socket(object):
	def __init__(self):
		self.tcp_sock = socket(AF_INET, SOCK_STREAM)
		self.udp_sock = socket(AF_INET, SOCK_DGRAM)
		self.udp_sock.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
		self.addr = gethostbyname(gethostname())

	def broadcast(self):
		self.udp_sock.sendto("Profit_Cut?".encode(), ('255.255.255.255', 12222))
		data, addr = self.udp_sock.recvfrom(200)
		if data.decode() == "Profit_OK":
			return data, addr
		else:
			return False

	def tcp_connect(self, addr):
		self.tcp_sock.connect((addr, 12223))
		chain = self.tcp_sock.recv(65535)

		return chain

	def disconnect(self, sock):
		sock.close()


sock = server_socket()
data, addr = sock.broadcast()
print(data, addr)

if data:
	chain = sock.tcp_connect(addr[0])
	print(chain)


else:
	print(False)
