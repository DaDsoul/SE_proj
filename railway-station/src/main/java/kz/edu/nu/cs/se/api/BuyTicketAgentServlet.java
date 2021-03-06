package kz.edu.nu.cs.se.api;

import com.google.gson.Gson;
import kz.edu.nu.cs.se.api.utils.TicketRequestObject;
import kz.edu.nu.cs.se.dao.AgentController;
import kz.edu.nu.cs.se.dao.TicketController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.logging.Logger;

import static kz.edu.nu.cs.se.api.utils.JWTUtils.*;

@WebServlet(urlPatterns = {"/myrailway/agent/buy-ticket"})
public class BuyTicketAgentServlet extends HttpServlet {

    Logger logger = Logger.getLogger(BuyTicketAgentServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TicketRequestObject ticketRequestObject = new Gson().fromJson(request.getReader(), TicketRequestObject.class);
        String token = ticketRequestObject.getToken();

        logger.info(String.format("Received token: %s", token));

        if (isExpired(token)){
            logger.warning("Token has expired");
            response.sendError(401, "Token has expired");
        }

        if (!isAgent(token)) {
            logger.warning("Permission denied, not an agent");
            response.sendError(401, "[ERROR] Permission denied, not an agent");
            return;
        }

        String agentUsername = getUserFromToken(token);

        Optional<String> optionalEmail = AgentController.getEmailByUsername(agentUsername);
        if (!optionalEmail.isPresent()) {
            logger.severe(String.format("[ERROR] Failed to fetch email for username=%s%n", agentUsername));
            response.sendError(500, "[ERROR] Failed to fetch email");
            return;
        }

        String agentEmail = optionalEmail.get();
        Optional<Integer> dummyUserID = AgentController.getDummyUserID(agentEmail);
        if (!dummyUserID.isPresent()) {
            logger.severe(String.format("Failed to fetch dummyUserID"));
            response.sendError(500, "[ERROR] Failed to fetch dummyUserID");
            return;
        }

        Integer scheduleId = ticketRequestObject.getScheduleId();
        Integer origin_id = ticketRequestObject.getOrigin_id();
        Integer destination_id = ticketRequestObject.getDestination_id();
        Integer owner_document_id = ticketRequestObject.getOwner_document_id();
        Float price = ticketRequestObject.getPrice();

        String start_date = ticketRequestObject.getStart_date();
        String end_date = ticketRequestObject.getEnd_date();
        String owner_document_type = ticketRequestObject.getOwner_document_type();
        String owner_firstname = ticketRequestObject.getOwner_firstname();
        String owner_lastname = ticketRequestObject.getOwner_lastname();

        String ticketStatus = "APPROVED";
        Optional<Integer> ticketModel = TicketController.BuyTicket(scheduleId, dummyUserID.get(), origin_id, destination_id, price,
                start_date, end_date, owner_document_type, owner_document_id,owner_firstname,
                owner_lastname, ticketStatus);
        PrintWriter out = response.getWriter();

        if (ticketModel.isPresent()) {
            response.setStatus(200);
            out.append(new Gson().toJson(ticketModel.get()));
        } else {
            response.setStatus(501);
        }

        out.flush();
    }
}
