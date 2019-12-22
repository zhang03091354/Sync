public class Main {

    public static void main(String[] args) {
        TestList testHandle = new TestList();
        System.gc();

        if(true){
            testHandle.pciSync_backup();
            //testHandle.rsyncSync_backup();
        }else if(false){
            //
        }else{
            //testHandle.testWrite();
            //testHandle.checkRsyncDataStore();
            //testHandle.testString();
            testHandle.testHexToByte();
        }
    }
}
