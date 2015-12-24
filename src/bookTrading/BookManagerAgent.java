import java.util.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

/**
 * Created by wangqisen on 2015/12/23.
 */
public class BookManagerAgent extends Agent {

    private AID[] sellers;
	private List<String> buyerOrders=new ArrayList<String>();

    @Override
    protected void setup() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-manager");
        sd.setName("JADE-book-trading");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new TickerBehaviour(this, 2000) {
			private static final long serialVersionUID = 1L;
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("book-selling");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    sellers = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        sellers[i] = result[i].getName();
                        System.out.println("manager find:"+sellers[i].getName());
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });

        addBehaviour(new QueryRequestsServer());
		
		addBehaviour(new AcceptOrdersServer());
    }

    @Override
    protected void takeDown() {
        System.out.println("Book-Manager-agent " + getAID().getName() + " terminating.");
    }

    private class QueryRequestsServer extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                try {
                    reply.setContentObject(sellers);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
	
	private class AcceptOrdersServer extends CyclicBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                buyerOrders.add(content);
				System.out.println("manager's monitoring all orders:");
				for(String s:buyerOrders){
					System.out.println("[history orders]"+s);
				}
            } else {
                block();
            }
        }
    }


}
