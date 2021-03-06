package kz.edu.nu.cs.se.api;

import com.google.gson.Gson;
import kz.edu.nu.cs.se.api.utils.AssignTicketToAgentObject;
import kz.edu.nu.cs.se.api.utils.JWTUtils;
import kz.edu.nu.cs.se.api.utils.Token;
import kz.edu.nu.cs.se.dao.AgentController;
import kz.edu.nu.cs.se.dao.PassengerController;
import kz.edu.nu.cs.se.dao.TicketController;
import kz.edu.nu.cs.se.model.TicketModel;
import kz.edu.nu.cs.se.model.User;
import kz.edu.nu.cs.se.view.Ticket;
import kz.edu.nu.cs.se.view.TicketForAgent;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static kz.edu.nu.cs.se.api.utils.JWTUtils.*;

@WebServlet(urlPatterns = {"/myrailway/agent/assign-ticket"})
public class AssignTicketToAgentServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AssignTicketToAgentServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        AssignTicketToAgentObject ticketToAgentObject = new Gson().fromJson(request.getReader(),
                AssignTicketToAgentObject.class);

        String token = ticketToAgentObject.getToken();

        if (isExpired(token)){
            logger.warning("Token has expired");
            response.sendError(401, "Token has expired");
            return;
        }

        if (!isAgent(token)) {
            logger.warning("Permission denied, not an agent");
            response.sendError(401, "[ERROR] Permission denied, not an agent");
            return;
        }

        String agentUsername = getUserFromToken(token);

        Optional<Integer> optionalAgentID = AgentController.getAgentIDByUsername(agentUsername);

        if (!optionalAgentID.isPresent()) {
            logger.severe("Failed to fetch agentID for username: " + agentUsername);
            response.sendError(401, "[ERROR] Failed to fetch agentID for username: " + agentUsername);
            return;
        }

        Integer ticketID = ticketToAgentObject.getTicketID();

        Boolean result = TicketController.assignTicketToAgent(optionalAgentID.get(), ticketID);

        Gson responseJSON = new Gson();

        Optional<Integer> optionalStationId = AgentController.getAgentStationID(optionalAgentID.get());

        if (!optionalStationId.isPresent()) {
            logger.severe("Failed to fetch stationID for agentID: " + optionalAgentID.get());
            response.sendError(401, "[ERROR] Failed to fetch stationID for agentID: " + optionalAgentID.get());
            return;
        }

        Integer stationId = optionalStationId.get();

        logger.info(String.format("[INFO] Fetching unapproved tickets for stationID=%d%n", stationId));

        ArrayList<TicketModel> ticketModels = TicketController.getUnapprovedTickets(stationId);
        ArrayList<TicketForAgent> tickets = ticketModels.stream().map(TicketForAgent::new).
                collect(Collectors.toCollection(ArrayList::new));

        logger.info(String.format("[INFO] Fetched %d unapproved tickets.%n", tickets.size()));

        HashMap<String, Object> responseDict = new HashMap<>();
        responseDict.put("data", tickets);
        responseDict.put("status", result ? "success" : "Failed to assign ticket to you.");

        PrintWriter out = response.getWriter();
        out.append(responseJSON.toJson(responseDict));
        out.flush();
    }
}