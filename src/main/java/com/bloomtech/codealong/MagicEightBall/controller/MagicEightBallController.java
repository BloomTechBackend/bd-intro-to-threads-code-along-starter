package com.bloomtech.codealong.MagicEightBall.controller;

import com.bloomtech.codealong.MagicEightBall.dao.MagicEightBallDao;
import com.bloomtech.codealong.MagicEightBall.model.MagicEightBallRequest;
import com.bloomtech.codealong.MagicEightBall.model.MagicEightBallResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RestController                        // This class contains RESTful controllers
public class MagicEightBallController {

    // Instantiate the Dao to be used to acquire data
    private MagicEightBallDao theEightBall = new MagicEightBallDao();

    /**
     * Handle HTTP POST requests for responses to questions
     *        with the URL: /magic8Ball/ask
     *
     * HTTP POST requests provide data for the request as JSON in the body of the request
     * The @RequestBody annotation tells Spring Boot to convert the JSON in the body to
     *     an object of the class specified
     *
     * @param theRequest - Will contain a JSON array of strings passed through the request body
     *                     each element in the array is a question to ask the Magic 8 Ball
     * @return           - A List of Magic8BallResponse objects each one containing
     *                     a question asked, the answer to the question and response time
     */
    @PostMapping(value="magic8ball/ask")
    public List<MagicEightBallResponse> getResponse(@RequestBody MagicEightBallRequest theRequest) throws InterruptedException {
        // Log the request to the server log
        System.out.println("-".repeat(100));
        logRequest("Request received via HTTP POST URL: /magic8ball/ask with " + theRequest.getQuestions().size() + " questions");

        long requestStartTime = System.currentTimeMillis();       // Remember the time at the start of request

        List<MagicEightBallResponse> answers = new ArrayList<>(); // Data store for responses

        for (String aQuestion : theRequest.getQuestions()) {                      // Loop through the questions received
            MagicEightBallResponse theResponse = new MagicEightBallResponse();    //    Instantiate an object to hold question response
            long startMilliseconds = System.currentTimeMillis();;                 //    Remember when processing started
            theResponse.setQuestion(aQuestion);                                   //    Copy the question to the response object
            try {
                theResponse.setAnswer(theEightBall.getResponse());                //    ask the question and store answer in response
            } catch (InterruptedException e) {                                    //    if exception occurs, handle it
                e.printStackTrace();                                              //       by printing the stack trace
                theResponse.setReturnCode(500);                                   //       set return code to indicate problem
                theResponse.setProcessingMessage("Magic 8 Ball processing error");//       set error message to return
            }
            // Calculate time to process response and store it in response object
            theResponse.setTimeToRespondSeconds(System.currentTimeMillis()-startMilliseconds);
            // copy question response object to list of answers/request response objects
            answers.add(theResponse);
        }
        // Log the end of request processing to the server log
        logRequest("Request completed in " + (System.currentTimeMillis() - requestStartTime) + " milliseconds");
        // Return a List of response objects with the question asked, answer and processing time from the Magic Eight Ball
        return answers;
        }
    /**
     * Handle HTTP Get requests for responses to questions
     *        with the URL: /magic8Ball/ask?question="value"
     *
     * HTTP GET requests provide data for the request as a value in in the URL
     * The @RequestParam annotation tells Spring Boot to convert the request parameter name in the URL to
     *     an object of the class specified
     *
     * @param question - Will contain the question to be asked as a query parameter
     * @return         - A Magic8BallResponse object
     */
    @GetMapping (value="magic8ball/ask")
    public MagicEightBallResponse getResponse(@RequestParam String question) throws InterruptedException {

        // Log the request to the server log
        logRequest("Request received via HTTP GET URL: /magic8ball/ask and question: " + question);
        long startMilliseconds = System.currentTimeMillis();                // Remember the time at the start of request

        MagicEightBallResponse theResponse = new MagicEightBallResponse();  // Data store for responses
        theResponse.setQuestion(question);                                  // Copy the question to the response object
        try {
            theResponse.setAnswer(theEightBall.getResponse());                // Ask the question and store answer in response
        } catch (InterruptedException e) {                                    // If exception occurs, handle it
            e.printStackTrace();                                              //    by printing the stack trace
            theResponse.setReturnCode(500);                                   //    set return code to indicate problem
            theResponse.setProcessingMessage("Magic 8 Ball processing error");//    set error message to return

        }
        // Calculate time to process response and store it in response object
        theResponse.setTimeToRespondSeconds(System.currentTimeMillis() - startMilliseconds);

        // Return response object with the question asked, answer and processing time from the Magic Eight Ball
        return theResponse;
    }

    /*****************************************************************************************
     * Helper method to log a message provided via parameter with timestamp
     *
     * @param message
     */
    private void logRequest(String message) {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        System.out.println(new Timestamp(datetime) + "\t--> " + message);
    } // End of logRequest() method

    /******************************************************************************************
     * Put any additional code required by the Code-Along here
     *****************************************************************************************/
    
}  // End of MagicEightBallController Class

