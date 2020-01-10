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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

/**
This example shows how to implement the responder role in
a FIPA-contract-net interaction protocol. In this case in particular
we use a <code>ContractNetResponder</code>
to participate into a negotiation where an initiator needs to assign
a task to an agent among a set of candidates.
@author Giovanni Caire - TILAB
*/
public class RandomContractNetResponderAgent extends Agent {
	private int[] competences;
	private boolean available = true;


	public RandomContractNetResponderAgent() {
		competences = new int[Parameters.nbContraintes];
		for(int i = 0; i < Parameters.nbContraintes; ++i) {
			competences[i] = Number.get();
		}
	}


	protected void setup() {
		String str = "";
		for (int k = 0; k < competences.length ; ++k) {
			str += competences[k];
		}
		System.out.println("Agent "+getLocalName()+" ("+str+") waiting for CFP...");
		MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
		MessageTemplate.MatchPerformative(ACLMessage.CFP) );

		addBehaviour(new ContractNetResponder(this, template) {
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				String content = cfp.getContent();
				if(content.equals("end"))
				{
					System.out.println("Agent "+getLocalName()+" killing itself.");
					doDelete();
					return null;
				}
				else {
					Task t = new Task(content);
					System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+content+". Value is "+t.value()+".");
					int proposal = t.evaluateAction(competences);
					if (available && Math.random() >= 0.5) {
						// We provide a proposal
						System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
						ACLMessage propose = cfp.createReply();
						propose.setPerformative(ACLMessage.PROPOSE);
						propose.setContent(String.valueOf(proposal));
						return propose;
					}
					else {
						// We refuse to provide a proposal
						System.out.println("Agent "+getLocalName()+": Refuse");
						throw new RefuseException("evaluation-failed");
					}
				}
			}

			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
				System.out.println("Agent "+getLocalName()+": Proposal accepted");

				available = false;
				Task t = new Task(cfp.getContent());
				int proposal = t.evaluateAction(competences);
				System.out.println("Agent "+getLocalName()+": Action performed -> "+proposal);
				ACLMessage inform = accept.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			}

			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		} );
	}
}
