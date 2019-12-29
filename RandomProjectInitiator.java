package examples.JINProject;

import jade.core.Agent;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

public class RandomProjectInitiator extends Agent {
  protected void setup() {
    Object[] args = getArguments();
    if (args != null && args.length > 2) {
      Parameters.nbResponders = Integer.parseInt((String)args[0]);
      Parameters.nbContraintes = Integer.parseInt((String)args[1]);
      Parameters.nbTasks = Integer.parseInt((String)args[2]);
      if(args.length > 3) {
        Number.setIndex(Integer.parseInt((String)args[3]));
      }
      if(args.length > 4) {
        Number.setStep(Integer.parseInt((String)args[4]));
      }
    }

    PlatformController container = getContainerController();
    try {
      String[] responderNames = new String[Parameters.nbResponders];
      for (int i = 0; i < Parameters.nbResponders; ++i) {
        String localName = "random_responder_"+i;
        responderNames[i] = localName;
        AgentController responder = container.createNewAgent(localName, "examples.JINProject.RandomContractNetResponderAgent", null);
        responder.start();
      }

      Thread.sleep(1000);

      AgentController initiator = container.createNewAgent("random_initiator", "examples.JINProject.RandomContractNetInitiatorAgent", responderNames);
      initiator.start();

      Thread.sleep(1000);

      while(true) {
        try {
          if(initiator.getState().getName()=="Deleted") {
            break;
          }
        }
        catch (jade.wrapper.StaleProxyException e) {
          break;
        }
      }
      // System.out.println("RandomProjectInitiator killing itself.");
      doDelete();
    }
    catch (Exception e) {
      System.err.println( "Exception while adding agents: " + e );
      e.printStackTrace();
    }
  }
}
