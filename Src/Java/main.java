package votechain;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

class ThreadHandler extends Thread{
	vote_block vb;
	block_header bh;

	public block_header getBh() {
		return bh;
	}

	public ThreadHandler(vote_block vb, block_header bh) {
		super();
		this.vb = vb;
		this.bh = bh;
	}

	public void run() {
		vb.proof_of_work(bh);
	}

}
public class main {

	public static void main(String[] args) throws InterruptedException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		ServerSocket tcp_sock = new ServerSocket(12223);
		DatagramSocket udp_sock = new DatagramSocket(12222);
		ArrayList<Object> chain = new ArrayList<>();
		LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
		ArrayList<HashMap> current_transactions = new ArrayList<>();
		ArrayList<String> voters = new ArrayList<>();
		ArrayList<String> candidates = new ArrayList<>();
		HashMap<String, HashMap> users = new HashMap<>();
		ArrayList<ArrayList> chainlist = new ArrayList<>();
		HashMap<String, Object> map = new HashMap<>();
		ArrayList<InetAddress> addrlist = new ArrayList<>();
		
		map.put("token", 1);
		getRsa rsa = new getRsa();
		map.put("Key", rsa.get_public());
		users.put("201311105", (HashMap) map.clone());
		users.put("201311111", (HashMap) map.clone());
	
	
		vote_block vb = new vote_block( merkle_tree, current_transactions, voters, users);

		HashMap transac = vb.new_transaction("201311105", "A");
		HashMap transac2 = vb.new_transaction("201311111", "B");
		
		vb.setCurrent_transactions(current_transactions);
		
		vote_chain vc = new vote_chain(chain);
		genesisblock_header gh = new genesisblock_header("V1.0.0", vb.deadline(2018, 12, 12, 12));
		genesisblock gb = new genesisblock("201311105", "รัวะ", candidates, gh);
		vc.add_genesis(gb);
		block_header bh;
		if(chain.get(chain.size()-1) instanceof genesisblock) bh = new block_header("V.1.0.0", chain.size()+1, ((genesisblock)chain.get(chain.size()-1)).getBlock_hash(),merkle_tree.get(1));
		else bh = new block_header("V.1.0.0", chain.size(), ((block)chain.get(chain.size()-1)).getBlock_hash(), merkle_tree.get(1));

		vb.add_transaction(transac, bh);
		

		ThreadHandler th = new ThreadHandler(vb,  bh);
		th.start();
		th.join();
		
		
		vb.add_transaction(transac2, bh);

		vc.reslove_conflicts(chainlist);
		block b = new block(current_transactions, merkle_tree, bh);

		
		do {
			System.out.println(bh.toString());
			b = new block(current_transactions, merkle_tree, bh);
		}while(!vb.valid_proof(bh));
		System.out.println(vb.hash(bh.toString()).equals(b.getBlock_hash()));
		System.out.println(bh.getProof());
		System.out.println(b.getBlock_h().getProof());
		System.out.println(b.getBlock_hash());
		System.out.println(vb.hash(b.getBlock_h().toString()));
		System.out.println(b.getBlock_hash().equals(vb.hash(b.getBlock_h().toString())));

		
		if(vb.valid_block(b, chain)) {
			vc.add_block(b);
			current_transactions = new ArrayList<>();
		}
		System.out.println(chain.toString());

		
		server_socket ss = new server_socket(vb, vc, tcp_sock, udp_sock, chain, current_transactions, "รัวะ", users);
		client_socket cs = new client_socket("รัวะ", udp_sock, "201311100", chainlist, addrlist);
		System.out.println(1);
		Thread th2 = new Thread(ss);
		th2.start();
		System.out.println(1);
		cs.broadcast_verify();
		Thread.sleep(1000);
		System.out.println(addrlist.toString());
		
		System.out.println(chainlist.size());
//		PrivateKey pk = (PrivateKey)rsa.get_private();
//		cs.broadcast_newbie((PublicKey) rsa.get_public(), pk);
//		Thread.sleep(1000);
//		System.out.println(users.toString());
//		HashMap transac3 = vb.new_transaction("201311100", "c");
//		cs.broadcast_transac(transac3, pk);
//		Thread.sleep(1000);
//		System.out.println(current_transactions);
//		Thread.sleep(1000);
//		System.out.println(addrlist.toString());

	}
	

}
