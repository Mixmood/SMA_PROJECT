package examples.JINProject;

import jade.core.Agent;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class ProjectStatisticsWritter extends Agent {
  private PlatformController container;


  protected void setup() {
    long time = System.currentTimeMillis();

    // Initialisation of out files
    String enTete = "Contractants ; Contraintes ; Tâches ; Valeur ; Tâches terminées ; Valeur ; Satisfaction ; Rapport global ; Rapport tâches terminées \n";

    try {
      String JINFile = "JINOutput.csv";
      Parameters.JINOutput = new DataOutputStream(new FileOutputStream(JINFile));
      Parameters.JINOutput.writeChars(enTete);

      String RandomFile = "RandomOutput.csv";
      Parameters.RandomOutput = new DataOutputStream(new FileOutputStream(RandomFile));
      Parameters.RandomOutput.writeChars(enTete);


      Object[] args = getArguments();
      int nIteration = 10;
      if (args != null && args.length > 0) {
        nIteration = Integer.parseInt((String)args[0]);
      }

      container = getContainerController();

      for(int i = 0; i < nIteration; ++i) {
        launchOne();
        Thread.sleep(1000);
      }

      System.out.println("Temps d'exécution (ms) : "+String.valueOf(System.currentTimeMillis()-time));

      System.out.println("ProjectStatisticsWritter killing itself.");
      doDelete();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void launchOne() {
    // We create 2 runs : one with our agents, another with random agent but with the same parameters/constraints/skills
    System.out.println("###################################\n###################################");
    Parameters.nbResponders = (int)(Math.random() * 1000)+1;
    Parameters.nbContraintes = (int)(Math.random() * 48)+3;
    Parameters.nbTasks = (int)(Math.random() * 1000)+1;

    int startIndex = (int)(Math.random() * 12000);
    Number.setIndex(startIndex);
    Number.setStep((int)(Math.random() * 10)+1);

    try {
      // Creation of JINProjectInitiator
      AgentController JINinitiator = container.createNewAgent("JIN_project_initiator_", "examples.JINProject.JINProjectInitiator", null);
      JINinitiator.start();

      Thread.sleep(1000);

      while (true) {
        try {
          if(JINinitiator.getState().getName()=="Deleted") {
            break;
          }
        }
        catch (jade.wrapper.StaleProxyException e) {
          break;
        }
      }

      Number.setIndex(startIndex);

      // Creation of RandomProjectInitiator
      AgentController RANDOMinitiator = container.createNewAgent("random_project_initiator", "examples.JINProject.RandomProjectInitiator", null);
      RANDOMinitiator.start();

      Thread.sleep(1000);

      while(true) {
        try {
          if(RANDOMinitiator.getState().getName()=="Deleted") {
            break;
          }
        }
        catch (jade.wrapper.StaleProxyException e) {
          break;
        }
      }
    }
    catch(Exception e) {
      System.err.println( "Exception while adding agents: " + e );
      e.printStackTrace();
    }
  }
}
