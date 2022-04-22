public class UpdateDB {
    public static void main(String[] args) {
        FireBaseManager fireBaseManager = new FireBaseManager();
        fireBaseManager.pushFromDump();
        System.out.println("Java is exiting...");
    }
}
