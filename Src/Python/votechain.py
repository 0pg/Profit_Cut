import hashlib
import json
from math import log2
from time import time
from uuid import uuid4
from collections import OrderedDict

class VoteChain(object):
	def __init__(self):
		self.merkle_tree = OrderedDict()
		self.chain = []
		self.current_transactions = []
		self.voters = set()

	'''
		체인의 첫번째 블록
		:param
		constructor : 첫번째 블록을 생성한 계정
		subject : 해당 블록 체인의 주제
	'''

	def genesis_block(self, constructor, subject):
		block_header = {
			'index' : 1,
			'constructor' : constructor,
			'timestamp' : time(),
		}
		block_hash = self.hash(block_header)
		block = {
			'block_hash' : block_hash,
			'block_header' : block_header,
			'subject' : subject,
		}
		self.chain.append(block)
	
	'''
		체인의 주제 반환
	'''
	def get_subject(self):
		return self.chain[0]['subject']

	'''
		블럭의 구조 정의
	'''	
	def new_block(self):
		self.merkle_tree = self.transaction_record()
		block_header = {
			'index' : len(self.chain) + 1,
			'timestamp' : time(),
			'proof' : 0, 
			'previous_hash' : self.chain[-1]['block_hash'],
			'merkle_root': self.merkle_tree[1],
		}
		block_header = self.proof_of_work(block_header)
		block_hash = self.hash(block_header)
		block = {
			'block_hash' : block_hash,
			'block_header' : block_header, 
			'transactions' : self.current_transactions,
			'merkle_tree' : self.merkle_tree,
			}
		self.current_transactions = []
		self.chain.append(block)
		
		return block
	
	'''
		투표 완료계정 확인
		:params
		voters : 이미 투표에 참여한 계정 집합
	'''
	def update_voters(self, voters):
		self.voters.update(voters)

	'''
		계정이 투표 완료계정 집합에 포함되어 있는지 확인
		:params
		voter : 새로운 transaction을 생성하고자 하는 계정
	'''
	def check_voters(self, voter):
		if voter in self.voters:
			return False
		return True

	'''
		새로운 거래내용 추가
		:param
		voter : 투표자
		candidate : 후보자
	'''

	def new_transaction(self, voter, candidate):
		self.current_transactions.append({
			'voter' : voter,
			'choosen candidate' : candidate,
			})

		return self.last_block['block_header']['index'] + 1

	'''
		거래내용을 sha256 해시함수를 사용해 변환
		-OrderedDict 형태로 저장되며, root node 의 index를 1로 시작하여 BFS 알고리즘에 따라 노드에 부여한 index를 key로 가짐
		:param
		merkle_tree : transaction들의 hash tree
	'''
	def transaction_record(self):
		merkle_tree = OrderedDict()
		transactions = list(self.current_transactions)
		length = len(transactions)
		dep = int(log2(length))
		nodes = pow(2,dep)
		extra_nodes = length - nodes
		non = pow(2,dep+1)
		
		for index in range(0, extra_nodes):
			left = transactions.pop(index)
			right = transactions.pop(index)
			left_hash = self.hash(left)
			right_hash = self.hash(right)
			merkle_tree[non] = left_hash
			non += 1
			merkle_tree[non] = right_hash
			non += 1
			transactions.insert(index, left_hash+right_hash)

		while True:  
			length = len(transactions)
			dep = int(log2(length))
			nodes = non = pow(2,dep)      
			
			for index in range(0, int(nodes/2)):
				left = transactions.pop(index)
				left_hash = self.hash(left)
				merkle_tree[non] = left_hash
				non += 1
				right = transactions.pop(index)
				right_hash = self.hash(right)
				merkle_tree[non] = right_hash
				non += 1
				transactions.insert(index, left_hash+right_hash)
		
			if length is 1: 
				merkle_tree[1] = self.hash(transactions)
				return merkle_tree
	'''
		블럭체인의 nonce 값을 구하는 과정. proof 값을 1씩 증가시키며 적합한 해시값을 찾을 때 까지 반복
		:param
		block_h : 블럭의 헤더정보
	'''
	def proof_of_work(self, block_h):
		while self.valid_proof(block_h) is False:
			block_h['proof'] += 1

		return block_h

	'''
		블럭체인이 유효한지 검증하는 과정
		:param
		chain : 현재 블럭들이 포함된 리스트
	'''
	def valid_chain(self, chain):
		last_block = chain[0]
		current_index = 1

		while current_index < len(chain):
			block = chain[current_index]
			block_h = block['block_header']
			print("{}".format(last_block))
			print("{}".format(block))
			print("\n------------\n")

			if block_h['previous_hash'] != hash(last_block['block_header']):
				return False

			if not self.valid_proof(block_h):
				return False

			last_block = block
			current_index += 1
		
		return True

	'''
		블럭이 분기되었을 때 충돌을 해결. 연결된 블럭의 길이가 긴 쪽의 체인이 유지된다.
	'''
	def resolve_conflicts(self, new_chain):
		max_length = len(self.chain)
		length = len(new_chain)

		if length > max_length and self.valid_chain(new_chain):
			self.chain = new_chain
			return True

		return False

	'''
		블럭의 해시값을 구하는 과정. 해시값의 첫 4자리가 '0000'이면 적합한 것으로 함
		:param
		blokc_h : 블럭의 헤더정보
	'''
	def valid_proof(self, block_h):
		guess_hash = self.hash(block_h)
		
		if guess_hash[ :4] == "0000":
			return guess_hash
		
		else:
			return False

	'''
		인자로 넘어온 값의 sha256 해시를 반환
	'''
	@staticmethod
	def hash(key):
		key_string = json.dumps(key, sort_keys=True).encode()
		return hashlib.sha256(key_string).hexdigest()

	'''
		현재 체인의 끝 블록을 반환
	'''
	@property
	def last_block(self):
		return self.chain[-1]

	def test(self):
		print(self.chain)

'''
chain = VoteChain()
chain.test()
chain.genesis_block('201311105', '총학선거')
chain.new_transaction('1', 'A')
chain.new_transaction('2', 'A')
chain.new_block()
chain.test()
chain.new_transaction('3', 'B')
chain.new_transaction('4', 'C')
chain.new_block()
chain.test()
'''
