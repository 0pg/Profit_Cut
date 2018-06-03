package profitcut.votechain;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

class genesisblock_header implements Serializable{
    private String ver;
    private int index;
    private float time;
    private float deadline;

    public genesisblock_header(String ver, int index, float time, float deadline) {
        super();
        this.ver = ver;
        this.index = index;
        this.time = time;
        this.deadline = deadline;
    }
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

    public String header() {
        return ver + index + time + deadline;
    }
    @Override
    public String toString() {
        return "[ver=" + ver + ", index=" + index + ", time="
                + time + ", deadline=" + deadline + "]";
    }

}

class genesisblock implements Serializable{
    private String block_hash;
    private String subject;
    private String constructor;
    private ArrayList<String> candidates;
    private genesisblock_header genesis_h;

    public genesisblock(String block_hash, String subject, String constructor, ArrayList<String> candidates,
                        genesisblock_header genesis_h) {
        super();
        this.block_hash = block_hash;
        this.subject = subject;
        this.constructor = constructor;
        this.candidates = candidates;
        this.genesis_h = genesis_h;
    }

    public genesisblock(String constructor,String subject, ArrayList candidates, genesisblock_header genesis_h) {
        super();
        this.block_hash = vote_block.hash(genesis_h.header());
        this.subject = subject;
        this.constructor = constructor;
        this.candidates = candidates;
        this.genesis_h = genesis_h;
    }

    public ArrayList<String> getCandidates() {
        return candidates;
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
                + ", candidates="+candidates.toString()+ "]";
    }


}

class block_header implements Serializable{
    private String ver;
    private int index;
    private int proof;
    private float time;
    private String previous_hash;
    private String merkle_root;

    public block_header(String ver, int index, int proof, float time, String previous_hash, String merkle_root) {
        super();
        this.ver = ver;
        this.index = index;
        this.proof = proof;
        this.time = time;
        this.previous_hash = previous_hash;
        this.merkle_root = merkle_root;
    }
    public block_header(String ver, int index, float time, String previous_hash, String merkle_root) {
        super();
        this.ver = ver;
        this.index = index;
        this.proof = 0;
        this.time = time;
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
        this.time = Calendar.getInstance().getTimeInMillis() / 1000;
    }
    public String header() {
        return ver + index + proof + time + previous_hash + merkle_root;
    }

    @Override
    public String toString() {
        return "[ver=" + ver + ", index=" + index + ", proof=" + proof + ", time=" + time
                + ", previous_hash=" + previous_hash + ", merkle_root=" + merkle_root + "]";
    }
}

class block implements Serializable{
    private String block_hash;
    private ArrayList<HashMap> transaction_pool;
    private HashMap<Integer, String> merkle_tree;
    private block_header block_h;


    public block(String block_hash, ArrayList<HashMap> transaction_pool, HashMap<Integer, String> merkle_tree,
                 block_header block_h) {
        super();
        this.block_hash = block_hash;
        this.transaction_pool = transaction_pool;
        this.merkle_tree = merkle_tree;
        this.block_h = block_h;
    }

    public block(ArrayList<HashMap> transaction_pool, HashMap<Integer, String> merkle_tree,
                 block_header block_h) {
        super();
        this.block_hash = vote_block.hash(block_h.header());
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

    public vote_chain(ArrayList<Object> chain) {
        super();
        this.chain = chain;
    }

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

            if(block_h.getPrevious_hash().equals(vote_block.hash(last_block.getBlock_h().header()))) return false;
            if(!vote_block.valid_proof(block_h)) return false;
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
    private LinkedHashMap<Integer, String> merkle_tree;
    private ArrayList<HashMap> current_transactions;
    private ArrayList<String> voters;
    private HashMap<String, Integer> nodes;

    public LinkedHashMap<Integer, String> getMerkle_tree() {
        return merkle_tree;
    }

    public vote_block(LinkedHashMap<Integer, String> merkle_tree, ArrayList<HashMap> current_transactions,
                      ArrayList<String> voters, HashMap<String, Integer> nodes) {
        super();
        this.merkle_tree = merkle_tree;
        this.current_transactions = current_transactions;
        this.voters = voters;
        this.nodes = nodes;
    }

    public void setCurrent_transactions(ArrayList<HashMap> current_transactions) {
        this.current_transactions = current_transactions;
    }

    public void setVoters(ArrayList voters) {
        this.voters = voters;
    }

    public void update_voters(String voter) {
        this.voters.add(voter);
    }

    public boolean check_voters(String voter) {
        int token = 10;
        if(this.voters.contains(voter)) {
            return false;
        }
        if(token <= 0) {
            return false;
        }
        return true;
    }

    public HashMap<String, String> new_transaction(String voter, String candidate) {
        HashMap<String, String> transac = new HashMap<String, String>();
        transac.put("voter", voter);
        transac.put("candidate", candidate);

        return transac;
    }

    public boolean add_transaction(HashMap<String, String> transac) {
        String voter = transac.get("voter");
        try {
            int token = this.nodes.get(voter);
        } catch(NullPointerException e) {

        }
        if(check_voters(voter)) {
            this.current_transactions.add(transac);
            transaction_record();
            update_voters(transac.get("voter"));
            this.nodes.put(voter, 1);
            return true;
        }
        return false;
    }


    public boolean valid_block(block block, ArrayList<Object> chain) {
        genesisblock_header genesis_h = ((genesisblock)chain.get(0)).getGenesis_h();
        block_header block_h = block.getBlock_h();
        if(chain.size() >= 2) {
            block_header last_h = last_block(chain).getBlock_h();
            if(block_h.getTime() > genesis_h.getDeadline()) {return false;}
            if(block_h.getIndex() != last_h.getIndex()+1) {return false;}
            if(!valid_proof(block_h)) {return false;}
            if(!block_h.getPrevious_hash().equals(hash(last_h.header()))) {return false;}
            if(!hash(block_h.header()).equals(block.getBlock_hash())) {return false;}

            return true;
        }
        else {
            if(block_h.getTime() > genesis_h.getDeadline()) { System.out.println(1);return false;}
            if(block_h.getIndex() != genesis_h.getIndex()+1) { System.out.println(2);return false;}
            if(!valid_proof(block_h)) { System.out.println(3);return false;}
            if(!block_h.getPrevious_hash().equals(hash(genesis_h.header()))) {System.out.println(4); return false;}
            if(!hash(block_h.header()).equals(block.getBlock_hash())) {System.out.println(5); return false;}

            return true;
        }
    }

    public boolean proof_of_work(block_header block_h) {
        while(!valid_proof(block_h)) {
            block_h.setProof(1);
        }
        return true;
    }

    public static boolean valid_proof(block_header block_h) {
            try {
           //     String guess_hash = hash(block_h.toString());
                if (hash(block_h.header()).substring(0, 4).equals("0000")) return true;
                else return false;
            } catch (NullPointerException e) {
                return false;
            }
    }

    public static float deadline(int year, int month, int day, int hour) {
        Calendar c = Calendar.getInstance();
        if(year < c.get(Calendar.YEAR)) return 0;
        if(month > 12 || month < 1) return 0;
        if(day > 31 || day < 1) return 0;
        if(hour > 23 || hour < 0) return 0;
        c.set(year, month, day, hour, 0, 0);
        return c.getTimeInMillis() / 1000;
    }

    public void transaction_record() {
        LinkedHashMap<Integer, String> merkle_tree = this.merkle_tree;
        ArrayList<HashMap> transaction = this.current_transactions;
        ArrayList<String> transac = new ArrayList<>();
        for(int i = 0; i < transaction.size(); i++) {
            transac.add(i, transaction.get(i).toString());
        }
        int length = transac.size();
        int dep = (int)baseLog(length, 2);
        int nodes = (int) Math.pow(2, dep);
        int extra_nodes = length - nodes;
        int non = (int) Math.pow(2, dep+1);
        for(int i = 0; i < extra_nodes; i++) {
            String left = transac.get(i);
            transac.remove(i);
            String right = transac.get(i);
            transac.remove(i);
            String left_hash = hash(left.toString());
            String right_hash = hash(right.toString());
            merkle_tree.put(non, left_hash);
            non += 1;
            merkle_tree.put(non, right_hash);
            non += 1;
            transac.add(i, left_hash + right_hash);
        }
        while(true) {
            length = transac.size();
            if(length == 1) {
                merkle_tree.put(1, hash(transac.get(0).toString()));
                break;
            }
            dep = (int)baseLog(length, 2);
            nodes = non = (int)Math.pow(2, dep);
            for(int i = 0; i < (int)nodes/2; i++) {
                String left = transac.get(i);
                transac.remove(i);
                String left_hash = hash(left.toString());
                merkle_tree.put(non, left_hash);
                non += 1;
                String right = transac.get(i);
                transac.remove(i);
                String right_hash = hash(right.toString());
                merkle_tree.put(i, right_hash);
                non += 1;
                transac.add(i, left_hash+right_hash);
            }

        }
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
    public block last_block(ArrayList<Object> chain) {
        return (block)chain.get(chain.size()-1);
    }
    public static double baseLog(double x, double base) {

        return Math.log10(x) / Math.log10(base);
    }
}
