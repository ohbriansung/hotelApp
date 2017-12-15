package hotelapp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Class HotelDataBuilder. Loads hotel info from input files to ThreadSafeHotelData (using multithreading). */
public class HotelDataBuilder extends HotelData {
	private ThreadSafeHotelData hdata; // the "big" ThreadSafeHotelData that will contain all hotel and reviews info
	private ExecutorService exec;

	/** Constructor for class HotelDataBuilder.
	 * @param data
	 */
	public HotelDataBuilder(ThreadSafeHotelData data) {
		this.hdata = data;
		this.exec = Executors.newFixedThreadPool(1);
	}

	/** Constructor for class HotelDataBuilder that takes ThreadSafeHotelData and
	 * the number of threads to create as a parameter.
	 * @param data
	 * @param numThreads
	 */
	public HotelDataBuilder(ThreadSafeHotelData data, int numThreads) {
		this.hdata = data;
		this.exec = Executors.newFixedThreadPool(numThreads);
	}

	/**
	 * Read the json file with information about the hotels and load it into the
	 * appropriate data structure(s).
	 * @param jsonFilename
	 */
	public void loadHotelInfo(String jsonFilename) {
		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject)parser.parse(new FileReader(jsonFilename));
			JSONArray arr = (JSONArray)obj.get("sr");
			for (JSONObject res : (Iterable<JSONObject>) arr) {
				JSONObject ll = (JSONObject) res.get("ll"); //ll is another json object

				hdata.addHotel(res.get("id").toString(), res.get("f").toString()
						, res.get("ci").toString(), res.get("pr").toString(), res.get("ad").toString()
						, Double.parseDouble(ll.get("lat").toString()), Double.parseDouble(ll.get("lng").toString()));
			}
		}
		catch  (FileNotFoundException e) {
			System.out.println("Exception while running the loadHotelInfo: Could not find file: " + jsonFilename);
		}
		catch (ParseException e) {
			System.out.println("Exception while running the loadHotelInfo: Can not parse a given json file.");
		}
		catch (IOException e) {
			System.out.println("Exception while running the loadHotelInfo: General IO Exception in readJSON");
		}
	}

	/** Loads reviews from json files. Recursively processes subfolders.
	 *  Each json file with reviews should be processed concurrently (you need to create a new runnable job for each
	 *  json file that you encounter)
	 *
	 *  Submit the Runnable inner class to the ExecutorService variable, so that one of the threads from the pool of
	 *  threads will start executing it.
	 *
	 *  @param dir
	 */
	public void loadReviews(Path dir) {
		try (DirectoryStream<Path> filesList = Files.newDirectoryStream(dir)) {
			for (Path file: filesList) {
				if (file.toString().contains(".json")) {
					try {
						exec.submit(new LoadPerReview(file.toString()));
					}
					catch (Exception e) {
						System.out.println("Exception while running the loadReviews: " + e);
					}
				}
				else {
					loadReviews(file); //recursively find all .json files
				}
			}
		}
		catch (IOException e) {
			System.out.println("Exception while running the loadReviews: " + e);
		}
	}

	/** Prints all hotel info to the file.
	 * 	Calls hdata's printToFile method.
	 * 	Shutdown the Executor after we load all the reviews.
	 */
	public void printToFile(Path filename) {
		exec.shutdown();

		try {
			exec.awaitTermination(1, TimeUnit.MINUTES);
			hdata.printToFile(filename);
		} catch (InterruptedException e) {
			System.out.println("Exception while running the printToFile: " + e);
		}
	}


	/**
	 * Inner class that implements Runnable for ExecutorService to execute.
	 *
	 * run() method allows multi-thread load reviews from json file simultaneously.
	 */
	public class LoadPerReview implements Runnable {
		private String jsonFilename;

		/**
		 * Constructor for class LoadPerReview.
		 * @param jsonFilename
		 */
		public LoadPerReview(String jsonFilename) {
			this.jsonFilename = jsonFilename;
		}

		/**
		 * Allows thread to load reviews.
		 */
		public void run() {
			JSONParser parser = new JSONParser();

			try {
				JSONObject obj = (JSONObject)parser.parse(new FileReader(jsonFilename));

				//get to the review unit
				JSONObject reviewDetails = (JSONObject)obj.get("reviewDetails");
				JSONObject reviewCollection = (JSONObject)reviewDetails.get("reviewCollection");
				JSONArray reviewArray = (JSONArray)reviewCollection.get("review");

				//add each review into Review TreeMap
				for (JSONObject review : (Iterable<JSONObject>) reviewArray) {
					String reviewTitle = review.get("title").toString().replaceAll("(\\r|\\n|)\\n", " ");
					String reviewText = review.get("reviewText").toString().replaceAll("(\\r|\\n|)\\n", " ");

					hdata.addReview(review.get("hotelId").toString(), review.get("reviewId").toString()			//String hotelId, String reviewId
							, Integer.parseInt(review.get("ratingOverall").toString())							//int rating
							, reviewTitle, reviewText															//String reviewTitle, String review
							, (!review.get("isRecommended").toString().toUpperCase().equals("NO"))				//boolean isRecom
							, review.get("reviewSubmissionTime").toString()										//String date
							, review.get("userNickname").toString());											//String username

				}
			}
			catch (FileNotFoundException e) {
				System.out.println("Exception while running the LoadPerReview: Could not find file: " + jsonFilename);
			}
			catch (ParseException e) {
				System.out.println("Exception while running the LoadPerReview: Can not parse a given json file: " + jsonFilename);
			}
			catch (IOException e) {
				System.out.println("Exception while running the LoadPerReview: General IO Exception in readJSON: " + jsonFilename);
			}
		}
	}
}
