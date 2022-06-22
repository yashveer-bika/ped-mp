package ped;

public class TestINT4 {
    Simulator int4_net;

    public TestINT4() {
        int4_net = new Simulator("data/INT4/", false, "vehMP");
    }

    public void testController() {
        System.out.println("\nTESTING CONTROLLER ON INT4\n");
        int4_net.runSim(false);

        System.out.println("\nFINISHED TESTING CONTROLLER ON INT4\n");

    }
}
