package kz.edu.nu.cs.se.api;

import com.google.gson.Gson;
import kz.edu.nu.cs.se.api.utils.JWTUtils;
import kz.edu.nu.cs.se.api.utils.Token;
import kz.edu.nu.cs.se.dao.ManagerController;
import kz.edu.nu.cs.se.dao.StationWorkersController;
import kz.edu.nu.cs.se.model.StationWorker;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import static kz.edu.nu.cs.se.api.utils.JWTUtils.isExpired;
import static kz.edu.nu.cs.se.api.utils.JWTUtils.isManager;

@WebServlet(urlPatterns = {"/myrailway/manager/get-station-workers"})
public class GetStationWorkersOfManagerServlet extends HttpServlet {

        private final static Logger logger = Logger.getLogger(GetStationWorkersOfManagerServlet.class.getName());

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

            String token = new Gson().fromJson(request.getReader(), Token.class).getToken();
            logger.info(String.format("Received token: %s", token));

            if (isExpired(token) && isManager(token)){
                logger.warning("[ERROR] Token has expired");
                response.sendError(401, "Token has expired");
            }

            String managerUserName = JWTUtils.getUserFromToken(token);
            int managerStationId = ManagerController.getManagerStationID(managerUserName);
            logger.info("Manager station id: " + managerStationId);

            ArrayList<StationWorker> stationWorkers = StationWorkersController
                    .getStationWorkers(managerStationId);

            PrintWriter out = response.getWriter();
            out.append(new Gson().toJson(stationWorkers));
            out.flush();
        }
    }


