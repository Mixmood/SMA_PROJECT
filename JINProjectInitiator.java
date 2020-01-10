package examples.JINProject;

import jade.core.Agent;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;
import jade.wrapper.State;

public class JINProjectInitiator extends Agent {
  protected void setup() {
    Object[] args = getArguments();
    // Setup of the parameters
    if (args != null && args.length > 2) {
      Parameters.nbResponders = Integer.parseInt((String)args[0]);
      Parameters.nbContraintes = Math.max(3,Integer.parseInt((String)args[1]));
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
      // Creation of responders
      for (int i = 0; i < Parameters.nbResponders; ++i) {
        String localName = "JIN_responder_"+i;
        responderNames[i] = localName;
        AgentController responder = container.createNewAgent(localName, "examples.JINProject.JINContractNetResponderAgent", null);
        responder.start();
      }

      Thread.sleep(1000);

      // Creation of initiator
      AgentController initiator = container.createNewAgent("JIN_initiator", "examples.JINProject.JINContractNetInitiatorAgent", responderNames);
      initiator.start();

      Thread.sleep(1000);

      // Awaiting death of initiator
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
      System.out.println("JINProjectInitiator killing itself.");
      doDelete();
    }
    catch (Exception e) {
      System.err.println( "Exception while adding agents: " + e );
      e.printStackTrace();
    }
  }
}
