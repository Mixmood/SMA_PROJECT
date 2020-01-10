package examples.JINProject;

/**
* This class is a data and utilitary class about Task
*/

public class Task {
  private int[] contraintes;

  // Random constructor
  public Task() {
    contraintes = new int[Parameters.nbContraintes];
    for(int i = 0; i < Parameters.nbContraintes; ++i) {
      contraintes[i] = Number.get();
    }
  }

  // Constructor with a string
  public Task(String str) {
    contraintes = new int[Parameters.nbContraintes];
    for(int i = 0; i < Parameters.nbContraintes; ++i) {
      contraintes[i] = Integer.parseInt(""+str.charAt(i));
    }
  }

  // Calculate the value of the task
  public int value() {
    int ret = 0;
    for(int i = 0; i < contraintes.length; ++i) {
      ret += contraintes[i];
    }
    return ret;
  }

  // Transform the task into string to send it to responders
  public String toMessage() {
    String ret = "";
    for(int i = 0; i < contraintes.length; ++i) {
      ret += contraintes[i];
    }
    return ret;
  }

  // This method evaluates the success rate of a task based on the skills of an agent
  public int evaluateAction(int[] competences) {
    int ret = 0;

		for(int i = 0; i < Parameters.nbContraintes; ++i) {
      ret += Math.min(contraintes[i], competences[i]);
    }

    return ret;
  }
}
