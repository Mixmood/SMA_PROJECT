package examples.JINProject;

import jade.core.Agent;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import java.util.concurrent.TimeUnit;

public class RandomProjectInitiator extends Agent {
  protected void setup() {
    Object[] args = getArguments();
    if (args != null && args.length > 2) {
      Parameters.nbResponders = Integer.parseInt((String)args[0]);
      Parameters.nbContraintes = Integer.parseInt((String)args[1]);
      Parameters.nbTasks = Integer.parseInt((String)args[2]);
      if(args.length == 4) {
        Number.setIndex(Integer.parseInt((String)args[3]));
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

      AgentController initiator = container.createNewAgent("random_initiator", "examples.JINProject.RandomContractNetInitiatorAgent", responderNames);
      initiator.start();
    }
    catch (Exception e) {
      System.err.println( "Exception while adding agents: " + e );
      e.printStackTrace();
    }
  }
}
