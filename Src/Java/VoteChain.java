package votechain;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;


class genesisblock_header {
	private String ver;
	private int index;
	private float time;
	private float deadline;
	public genesisblock_header(String ver, float deadline) {
		super();
		this.ver = ver;
		this.index = 1;
		this.time = Calendar.getInstance().getTimeInMillis() / 1000;
		this.deadline = deadline;
	}
	public int getIndex() {
		return index;
	}
	public float getTime() {
		return time;
	}
	public String getVer() {
		return ver;
	}
	public float getDeadline() {
		return deadline;
	}
	@Override
	public String toString() {
		return "[ver=" + ver + ", index=" + index + ", time="
				+ time + ", deadline=" + deadline + "]";
	}
	
}

class genesisblock {
	private String block_hash;
	private String subject;
	private String constructor;
	private genesisblock_header genesis_h;

	public genesisblock(String block_hash, String constructor,String subject, genesisblock_header genesis_h) {
		super();
		this.block_hash = block_hash;
		this.subject = subject;
		this.constructor = constructor;
		this.genesis_h = genesis_h;
	}

	public String getBlock_hash() {
		return block_hash;
	}

	public String getSubject() {
		return subject;
	}
	
	public String getConstructor() {
		return constructor;
	}
	
	public genesisblock_header getGenesis_h() {
		return genesis_h;
	}

	public void setGenesis_h(genesisblock_header genesis_h) {
		this.genesis_h = genesis_h;
	}

	@Override
	public String toString() {
		return "[genesis_header=" + genesis_h.toString() + ", block_hash=" + block_hash + ", subject=" + subject + ", constructor=" + constructor
				+ "]";
	}
	
	
}

class block_header {
	private String ver;
	private int index;
	private int proof;
	private float time;
	private String previous_hash;
	private String merkle_root;

	public block_header(String ver, int index, String previous_hash, String merkle_root) {
		super();
		this.ver = ver;
		this.index = index;
		this.proof = 0;
		this.time = Calendar.getInstance().getTimeInMillis() / 1000;
		this.previous_hash = previous_hash;
		this.merkle_root = merkle_root;
	}
	public int getIndex() {
		return index;
	}
	public String getVer() {
		return ver;
	}
	public int getProof() {
		return proof;
	}
	public float getTime() {
		return time;
	}
	public String getPrevious_hash() {
		return previous_hash;
	}
	public String getMerkle_root() {
		return merkle_root;
	}
	
	public void setMerkle_root(String merkle_root) {
		this.merkle_root = merkle_root;
	}
	public void setProof(int proof) {
		this.proof += proof;
	}
	@Override
	public String toString() {
		return "[ver=" + ver + ", index=" + index + ", proof=" + proof + ", time=" + time
				+ ", previous_hash=" + previous_hash + ", merkle_root=" + merkle_root + "]";
	}
}

class block{
	private String block_hash;
	private ArrayList<HashMap> transaction_pool;
	private HashMap<Integer, String> merkle_tree;
	private block_header block_h;

	
	public block(ArrayList<HashMap> transaction_pool, HashMap<Integer, String> merkle_tree,
			block_header block_h) {
		super();
		this.block_hash = vote_block.hash(block_h.toString());
		this.transaction_pool = transaction_pool;
		this.merkle_tree = merkle_tree;
		this.block_h = block_h;
	}
	
	public String getBlock_hash() {
		return block_hash;
	}
	public ArrayList<HashMap> getTransaction_pool() {
		return transaction_pool;
	}
	public HashMap<Integer, String> getMerkle_tree() {
		return merkle_tree;
	}
	public block_header getBlock_h() {
		return block_h;
	}

	public void setBlock_h(block_header block_h) {
		this.block_h = block_h;
	}

	@Override
	public String toString() {
		return "[block_header=" + block_h.toString() + ", block_hash=" + block_hash + ", transaction_pool=" + transaction_pool.toString() + ", merkle_tree="
				+ merkle_tree.toString() + "]";
	}
}
class vote_chain {
	ArrayList<Object> chain;
	
	public void add_genesis(genesisblock genesis) {
		this.chain.add(0, genesis);
	}
	
	public void add_block(block block) {
		this.chain.add(block);
	}
	
	public boolean valid_chain(ArrayList<Object> chain) {
		genesisblock genesis = (genesisblock) chain.get(0);
		block last_block = (block)chain.get(1);
		int current_index = 2;
		
		while(current_index < chain.size()) {
			block block = (block)chain.get(current_index);
			block_header block_h = block.getBlock_h();
			
			if(block_h.getPrevious_hash() != vote_block.hash(last_block.toString())) return false;
			if(vote_block.valid_proof(block_h) == "false") return false;
			if(block_h.getTime() > ((genesisblock_header)chain.get(0)).getDeadline()) return false;
			
			last_block = block;
			current_index += 1;
		}
		
		return true;
	}
	
	public void reslove_conflicts(ArrayList<ArrayList> chainlist) {
		for(ArrayList new_chain : chainlist) {
			int max_length = this.chain.size();
			int length = new_chain.size();
			if(length > max_length && valid_chain(new_chain)) {
				this.chain = new_chain;
			}
		}
	}
}

class vote_block {
	LinkedHashMap<Integer, String> merkle_tree;
	ArrayList<HashMap> current_transactions;
	ArrayList<String> voters;

	public void setMerkle_tree(LinkedHashMap<Integer, String> merkle_tree) {
		this.merkle_tree = merkle_tree;
	}

	public void setCurrent_transactions(ArrayList<HashMap> current_transactions) {
		this.current_transactions = current_transactions;
	}

	public void setVoters(ArrayList<String> voters) {
		this.voters = voters;
	}

	public vote_block(LinkedHashMap<Integer, String> merkle_tree,
			ArrayList<HashMap> current_transactions, ArrayList<String> voters) {
		super();
		this.merkle_tree = merkle_tree;
		this.current_transactions = current_transactions;
		this.voters = voters;
	}

	public void update_voters(String voter) {
		this.voters.add(voter);
	}
	
	public boolean check_voters(String voter) {
		for(String vote : this.voters) {
			if(vote == voter) {
				return false;
			}
		}
		return true;
	}
	
	public HashMap<String, String> new_transaction(String voter, String candidate) {
		HashMap<String, String> transac = new HashMap<String, String>();
		transac.put("voter", voter);
		transac.put("candidate", candidate);
		return transac;
	}
	
	public void add_transaction(HashMap<String, String> transac) {
		if(!check_voters(transac.get("voter"))) {
			this.current_transactions.add(transac);
			this.merkle_tree = transaction_record();
		}
	}
	
	public boolean valid_block(block block, ArrayList<Object> chain) {
		genesisblock_header genesis_h = (genesisblock_header)chain.get(0);
		block_header block_h = block.getBlock_h();
		block_header last_h = last_block(chain).getBlock_h();
		
		if(block_h.getTime() > genesis_h.getDeadline()) return false;
		if(block_h.getIndex() != last_h.getIndex()+1) return false;
		if(valid_proof(block_h) == "false") return false;
		if(block_h.getPrevious_hash() != hash(last_h.toString())) return false;
		
		return true;
	} 
	
	public block_header proof_of_work(block_header block_h) {
		if(valid_proof(block_h) == "false") {
			block_h.setProof(1);
			return null;
		}
		return block_h;
	}
	public static String valid_proof(block_header block_h) {
		String guess_hash = hash(block_h.toString());
		if(guess_hash.substring(0, 4) == "0000") 
			return guess_hash;
		else return "false";
		
	}
	
	public float deadline(int year, int month, int day, int hour) {
		Calendar c = Calendar.getInstance();
		if(year < c.get(Calendar.YEAR)) return 0;
		if(month > 12 || month < 1) return 0;
		if(day > 31 || day < 1) return 0;
		if(hour > 23 || hour < 0) return 0;
		c.set(year, month, day, hour, 0, 0);
		return c.getTimeInMillis()/1000;
	}
	
	public LinkedHashMap<Integer, String> transaction_record() {
		LinkedHashMap<Integer, String> merkle_tree = new LinkedHashMap<>();
		ArrayList<Object> transaction = (ArrayList<Object>) this.current_transactions.clone();
		int length = transaction.size();
		int dep = (int)baseLog(length, 2);
		int nodes = (int) Math.pow(2, dep);
		int extra_nodes = length - nodes;
		int non = (int) Math.pow(2, dep+1);
		for(int i = 0; i < extra_nodes; i++) {
			HashMap left = (HashMap) transaction.get(i);
			transaction.remove(i);
			HashMap right = (HashMap) transaction.get(i);
			transaction.remove(i);
			String left_hash = hash(left.toString());
			String right_hash = hash(right.toString());
			merkle_tree.put(non, left_hash);
			non += 1;
			merkle_tree.put(non, right_hash);
			non += 1;
			transaction.add(i, left_hash+right_hash);
		}
		while(true) {
			length = transaction.size();
			dep = (int)baseLog(length, 2);
			nodes = non = (int)Math.pow(2, dep);
			for(int i = 0; i < (int)nodes/2; i++) {
				HashMap left = (HashMap) transaction.get(i);
				transaction.remove(i);
				String left_hash = hash(left.toString());
				merkle_tree.put(non, left_hash);
				non += 1;
				HashMap right = (HashMap) transaction.get(i);
				transaction.remove(i);
				String right_hash = hash(right.toString());
				merkle_tree.put(i, right_hash);
				non += 1;
				transaction.add(i, left_hash+right_hash);
			}	
			if(length == 1) {
				merkle_tree.put(1, hash(transaction.get(0).toString()));
				return merkle_tree;
			}
		}
	}	
	
	public block_header calculate_proof(block_header block_h) {
		while((proof_of_work(block_h)) == null) {
			if(block_h.getMerkle_root() != this.merkle_tree.get(1)) {
				block_h.setMerkle_root(this.merkle_tree.get(1));
			}
		}
		return block_h;
	}
	
	public static double baseLog(double x, double base) {

		return Math.log10(x) / Math.log10(base);
	}

	public static String hash(String key) {
		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(key.getBytes("UTF-8"));
			
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return hexString.toString();
	}
	private block last_block(ArrayList<Object> chain) {
		return (block)chain.get(chain.size()-1);
	}

}
