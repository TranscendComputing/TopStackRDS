package com.msi.tough.rdsquery;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.msi.tough.core.Appctx;

/**
 * Servlet implementation class RDSQueryServlet
 */
@WebServlet(description = "Servlet for the Relational Database Service Query API", urlPatterns =
{
    "/rdsquery"
})


public class RDSQueryServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RDSQueryServlet()
    {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            final RDSQueryImpl impl = Appctx.getBean("rdsQueryImpl");
            impl.process(request, response);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException
    {
        this.doGet(request, response);
    }
}
