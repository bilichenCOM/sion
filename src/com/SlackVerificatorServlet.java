package com;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import model.Sion;
import utils.JsonParser;

@SuppressWarnings("serial")
@WebServlet("/")
public class SlackVerificatorServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.sendRedirect("/Cabinet");
		request.getRequestDispatcher("/prior").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("\r\nPOST to servlet");
		JSONObject requestBody = JsonParser.parseJson((HttpServletRequest) request);
		String type = requestBody.getString("type");
		if (type.equals("url_verification")) {
			handleSlackVerification(requestBody, (HttpServletResponse) response);
			return;
		}

		if (type != null) {
			Sion bot = Sion.getInstance();
			bot.handle(requestBody);
		}
	}

	private void handleSlackVerification(JSONObject requestBody, HttpServletResponse response) throws IOException {
		System.out.println("verification.....");
		String challenge = requestBody.getString("challenge");
		System.out.println("challenge=" + challenge);
		PrintWriter out = response.getWriter();

		response.setContentType("text/plain");
		response.setStatus(200);
		out.write(challenge);
		System.out.println("done!");
	}
}