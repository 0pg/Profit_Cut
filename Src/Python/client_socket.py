from socket import *
from threading import *
import json

class client_socket(object):
	def __init__(self):
		self.tcp_sock = socket(AF_INET, SOCK_STREAM)
		self.udp_sock = socket(AF_INET, SOCK_DGRAM)
		self.udp_sock.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
		self.addr = gethostbyname(gethostname())

	def broadcast_verify(self, subject):
		self.udp_sock.sendto(("Profit_Cut?"+subject).encode(), ('255.255.255.255', 12222))
		data, addr = self.udp_sock.recvfrom(200)
		if data.decode() == "Profit_OK":
			return data, addr
		else:
			return False

	def tcp_connect_recv(self, addr):
		self.tcp_sock.connect((addr, 12223))
		chain = self.tcp_sock.recv(65535)

		return chain

	def disconnect(self, sock):
		sock.close()


sock = client_socket()
data, addr = sock.broadcast_verify()
print(data, addr)

if data:
	chain = sock.tcp_connect_recv(addr[0])
	print(chain)


else:
	print(False)
