/**
* ***************************************************************
* JADE - Java Agent DEvelopment Framework is a framework to develop
* multi-agent systems in compliance with the FIPA specifications.
* Copyright (C) 2000 CSELT S.p.A.
*
* GNU Lesser General Public License
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation,
* version 2.1 of the License.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the
* Free Software Foundation, Inc., 59 Temple Place - Suite 330,
* Boston, MA  02111-1307, USA.
* **************************************************************
*/
package examples.JINProject;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;

import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
This example shows how to implement the initiator role in
a FIPA-contract-net interaction protocol. In this case in particular
we use a <code>ContractNetInitiator</code>
to assign a dummy task to the agent that provides the best offer
among a set of agents (whose local
names must be specified as arguments).
@author Giovanni Caire - TILAB
*/
public class JINContractNetInitiatorAgent extends Agent {
	private int nResponders;
	private Object[] args;

	private Task[] tasks;

	private int satisfaction = 0;
	private int value = 0;
	private int nActionPerformed = 0;
	private int valuePerformed = 0;

	public JINContractNetInitiatorAgent() {
		tasks = new Task[Parameters.nbTasks];
		for(int i = 0; i < Parameters.nbTasks; ++i) {
			tasks[i] = new Task();
		}
	}

	protected void setup() {
		// Read names of responders as arguments
		args = getArguments();
		if (args != null && args.length > 0) {
			nResponders = args.length;
			System.out.println("Trying to delegate "+tasks.length+" tasks to "+nResponders+" responders.");

			// On trie les tâches par ordre décroissant de valeur
			Comparator<Task> taskComparator = new Comparator<Task>() {
				public int compare(Task t1, Task t2) {
					return -Integer.compare(t1.value(), t2.value());
				}
			};
			Arrays.sort(tasks, taskComparator);

			handleTask(0);
		}
		else {
			System.out.println("No responder specified.");
		}
	}

	private void handleTask(int index) {
		if(index >= tasks.length || index >= nResponders) {
			for(int i = index; i < tasks.length; ++i) {
				value += tasks[i].value();
			}
			printResults();
			endProcess();
		}
		else {
			// System.out.println("---------------");
			// System.out.println("Tâche n°"+(index+1));
			// System.out.println(tasks[index].toMessage());
			// System.out.println("Valeur : "+tasks[index].value());
			value += tasks[index].value();
			// Fill the CFP message
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < args.length; ++i) {
				msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
			}
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			// We want to receive a reply in 10 secs
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent(tasks[index].toMessage());

			addBehaviour(new ContractNetInitiator(this, msg) {

				protected void handlePropose(ACLMessage propose, Vector v) {
					// System.out.println("Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
				}

				protected void handleRefuse(ACLMessage refuse) {
					// System.out.println("Agent "+refuse.getSender().getName()+" refused");
				}

				protected void handleFailure(ACLMessage failure) {
					if (failure.getSender().equals(myAgent.getAMS())) {
						// FAILURE notification from the JADE runtime: the receiver
						// does not exist
						// System.out.println("Responder does not exist");
					}
					else {
						// System.out.println("Agent "+failure.getSender().getName()+" failed");
					}
					// Immediate failure --> we will not receive a response from this agent
					nResponders--;
				}

				protected void handleAllResponses(Vector responses, Vector acceptances) {
					if (responses.size() < nResponders) {
						// Some responder didn't reply within the specified timeout
						// System.out.println("Timeout expired: missing "+(nResponders - responses.size())+" responses");
					}
					// Evaluate proposals.
					int bestProposal = -1;
					AID bestProposer = null;
					ACLMessage accept = null;
					Enumeration e = responses.elements();
					while (e.hasMoreElements()) {
						ACLMessage msg = (ACLMessage) e.nextElement();
						if (msg.getPerformative() == ACLMessage.PROPOSE) {
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							acceptances.addElement(reply);
							int proposal = Integer.parseInt(msg.getContent());
							if (proposal > bestProposal) {
								bestProposal = proposal;
								bestProposer = msg.getSender();
								accept = reply;
							}
						}
					}
					// Accept the proposal of the best proposer
					if (accept != null) {
						satisfaction+=bestProposal;
						// System.out.println("Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
						accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					}
				}

				protected void handleInform(ACLMessage inform) {
					++nActionPerformed;
					valuePerformed += tasks[index].value();
					// System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
					handleTask(index+1);
				}
			} );
		}
	}

	private void endProcess() {
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		for (int i = 0; i < args.length; ++i) {
			msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
		}
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		msg.setContent("end");
		send(msg);
		// System.out.println("Initiator killing itself");
		doDelete();
	}

	private void printResults() {
		try {
			if (Parameters.JINOutput!=null) {
				Parameters.JINOutput.writeChars(Parameters.nbResponders+";"+Parameters.nbContraintes+";"+Parameters.nbTasks+";"+value+";"+nActionPerformed+";"+valuePerformed+";"+satisfaction+";"+((double)satisfaction)/((double)value)+";"+((double)satisfaction)/((double)valuePerformed)+"\n");
			}
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}

		System.out.println("************************");
		System.out.println("Résultats (JIN) :");
		System.out.println(nResponders+" agents");
		System.out.println(Parameters.nbContraintes+" compétences/contraintes");
		System.out.println(tasks.length+" tâches -> valeur : "+value);
		System.out.println(nActionPerformed+" tâches accomplies -> valeur : "+valuePerformed);
		System.out.println("Valeur de réalisation (satisfaction) : "+satisfaction);
		System.out.println("Rapport de réussite globale : "+((double)satisfaction)/((double)value));
		System.out.println("Rapport de réussite des tâches terminées : "+((double)satisfaction)/((double)valuePerformed));
		System.out.println("************************");
	}
}
