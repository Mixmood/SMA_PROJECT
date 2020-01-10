package examples.JINProject;

import java.io.DataOutputStream;

public class Parameters {
  public static int nbResponders = 5;     // N
  public static int nbContraintes = 10;   // C
  public static int nbTasks = 4;          // T

  public static DataOutputStream JINOutput = null;    // The file to write our results
  public static DataOutputStream RandomOutput = null; // The file to write random results
}
