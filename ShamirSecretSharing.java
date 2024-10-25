import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONObject;
import java.math.BigInteger;

public class ShamirSecretSharing {

    // Method to decode the value based on the given base using BigInteger
    public static BigInteger decodeValue(int base, String value) {
        return new BigInteger(value, base);
    }

    // Method to perform Lagrange Interpolation at x = 0 using BigInteger
    public static BigInteger lagrangeInterpolation(List<BigInteger> xVals, List<BigInteger> yVals, BigInteger x) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < xVals.size(); i++) {
            BigInteger term = yVals.get(i);
            for (int j = 0; j < xVals.size(); j++) {
                if (i != j) {
                    // (x - xVals[j]) / (xVals[i] - xVals[j]) using BigInteger
                    BigInteger numerator = x.subtract(xVals.get(j));
                    BigInteger denominator = xVals.get(i).subtract(xVals.get(j));
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        // Ensure the result is a positive integer by taking the absolute value
        return result.abs();
    }

    // Method to find the secret (constant term) from a single test case JSON object
    public static BigInteger findSecret(JSONObject data) {
        JSONObject keys = data.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<BigInteger> xVals = new ArrayList<>();
        List<BigInteger> yVals = new ArrayList<>();

        // Iterate over the entries in the JSON data to extract the points (x, y)
        for (Object key : data.keySet()) {
            if (!key.equals("keys")) {
                BigInteger x = new BigInteger(key.toString());
                JSONObject point = data.getJSONObject((String) key);
                int base = point.getInt("base");
                String value = point.getString("value");
                BigInteger y = decodeValue(base, value);

                xVals.add(x);
                yVals.add(y);
            }
        }

        // Perform Lagrange Interpolation to find the constant term at x = 0
        return lagrangeInterpolation(xVals, yVals, BigInteger.ZERO);
    }

    public static void main(String[] args) {
        try {
            // Path to the JSON file
            String filePath = "/Users/manasverma/Downloads/input1.json";

            // Read JSON data from the file
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse the JSON data
            JSONObject data = new JSONObject(jsonData);

            // Iterate over each test case
            JSONObject testCases = data.getJSONObject("testCases");
            for (Object testCaseKey : testCases.keySet()) {
                JSONObject testCaseData = testCases.getJSONObject(testCaseKey.toString());

                // Find and print the secret constant term for this test case
                BigInteger secret = findSecret(testCaseData);
                System.out.println("The secret (constant term) for " + testCaseKey + " is: " + secret);
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
}