package examples.JINProject;

public class Task {
  private int[] contraintes;

  public Task() {
    contraintes = new int[Parameters.nbContraintes];
    for(int i = 0; i < Parameters.nbContraintes; ++i) {
      // contraintes[i] = (int) (Math.random() * 10);
      contraintes[i] = Number.get();
    }
  }

  public Task(String str) {
    contraintes = new int[Parameters.nbContraintes];
    for(int i = 0; i < Parameters.nbContraintes; ++i) {
      // contraintes[i] = (int) str.charAt(i);
      contraintes[i] = Integer.parseInt(""+str.charAt(i));
    }
  }

  public int value() {
    int ret = 0;
    for(int i = 0; i < contraintes.length; ++i) {
      ret += contraintes[i];
    }
    return ret;
  }

  public String toMessage() {
    String ret = "";
    for(int i = 0; i < contraintes.length; ++i) {
      ret += contraintes[i];
    }
    return ret;
  }

  public int evaluateAction(int[] competences) {
    int ret = 0;

		for(int i = 0; i < Parameters.nbContraintes; ++i) {
      ret += Math.min(contraintes[i], competences[i]);
    }

    return ret;
  }
}
