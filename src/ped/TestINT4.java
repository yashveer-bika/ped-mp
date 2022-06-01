package ped;

public class TestINT4 {
    Simulator int4_net;

    public TestINT4() {
        int4_net = new Simulator("data/INT4/", false, "vehMP");
    }

    public void testController() {
        System.out.println("\nTESTING CONTROLLER ON INT4\n");
        double timeStepSize = 60*60;
        double totalRunTime = 60*60*10;
        double toleranceTime = 0;
        int4_net.runSim( timeStepSize,  totalRunTime, toleranceTime);

        System.out.println("\nFINISHED TESTING CONTROLLER ON INT4\n");

    }
}
