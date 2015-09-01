package org.opencv.samples.colorblobdetect;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ColorBlobDetectionActivity extends Activity implements
		OnTouchListener, CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";
	long startTime = 0;
	long finishTime = 0;
	StringBuilder sb = new StringBuilder();
	private boolean mIsColorSelected = false;
	private Mat mRgba;
	private Scalar mBlobColorRgba;
	private Scalar mBlobColorHsv;
	private ColorBlobDetector mDetector;
	private Mat mSpectrum;
	private Size SPECTRUM_SIZE;
	private Scalar CONTOUR_COLOR;

	private CameraBridgeViewBase mOpenCvCameraView;
	long totalperiod = 60000;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView
						.setOnTouchListener(ColorBlobDetectionActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public ColorBlobDetectionActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.color_blob_detection_surface_view);

		// mOpenCvCameraView.set
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.enableFpsMeter();
		// VideoCapture vd = new VideoCapture();
		// vd.set( CV_CAP_PROP_FPS, 30 );

	}

	@Override
	public void onPause() {
		super.onPause();
		calculate();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	// 10100110
	public void calculate() {
		System.out.println("sb==== " + sb.toString());
		String seq = getBitPattern(sb.toString());

		// int occurance = search(seq, "101001100");
		// double accuracy = (occurance * 9.0) / (double) (seq.length());

		ParityChecking pc = new ParityChecking();
		String decodedData = pc.checkParity(seq.toCharArray());
		double errorRate = pc.getErrorRate();
		lookUpTable website = lookUpTable.FACEBOOK.get(decodedData);
		System.out.println(website);
		String url = new String("http://www." + website + ".com");

		System.out.println("DecodedData==>" + decodedData + "  " + url);

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);

		Context context = getApplicationContext();
		CharSequence text = "Input Sequence decoded: " + decodedData
				+ "\nWith Block error rate: " + errorRate;
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public int search(String seq, String pattern) {
		int index = 0;
		int foundPos = -1;
		int counter = 0;

		while (index < seq.length()) {
			if ((foundPos = seq.indexOf(pattern, index)) >= 0) {
				index += 4;
				counter++;

			} else if (index == 0 && foundPos < 0) {
				break;
			} else if (index > 0 && foundPos < 0) {
				break;
			} else {
				index++;
			}
		}

		return counter;

	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		// thr = new Mat(height, width, CvType.CV_8UC4);
		// thr1 = new Mat(height, width, CvType.CV_16UC4);
		mDetector = new ColorBlobDetector();
		mSpectrum = new Mat();
		mBlobColorRgba = new Scalar(255);
		mBlobColorHsv = new Scalar(255);
		SPECTRUM_SIZE = new Size(200, 64);
		CONTOUR_COLOR = new Scalar(255, 0, 0, 255);

		mBlobColorHsv.val[0] = 145.0;
		mBlobColorHsv.val[1] = 255.0;
		mBlobColorHsv.val[2] = 255.0;
		mBlobColorHsv.val[3] = 0.0;

		mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
		mDetector.setHsvColor(mBlobColorHsv);
		Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
		mIsColorSelected = true;
		startTime = SystemClock.elapsedRealtime();

	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	// int i = 0;
	MatOfPoint largestContour;

	// Rect tempRect = null;

	// private void saveImage(Mat src) {
	// int rtype = 0;
	// Imgproc.cvtColor(src, thr, Imgproc.COLOR_RGBA2GRAY);
	// Imgproc.threshold(thr, thr1, 180, 255, Imgproc.THRESH_BINARY);
	// File path = Environment
	// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	// System.out.println("path= " + path.getAbsolutePath());
	//
	// File file = new File(path, filename + "_" + i + ".png");
	//
	// filename = file.toString();
	// thr1.convertTo(thr1, rtype);
	// Boolean bool = Highgui.imwrite(filename, thr1);
	//
	// detectCircle(thr1);
	//
	// if (bool == true)
	// Log.d(TAG, "SUCCESS writing image to external storage");
	// else
	// Log.d(TAG, "Fail writing image to external storage");
	// i++;
	// int sum = 0;
	// d = src.diag();
	//
	// // for (int j = 0; j < d.rows(); j++) {
	// // // uchar* pixel = thr1.ptr<uchar>(i);
	// // // sum += thr1.at<>(j);
	// // thr1.get(row, col);
	// // }
	//
	// // for (int i = 0; i < d.rows; i++) {
	// //
	// // // const Double* Mi = d.ptr<Double>(i);
	// // // for(int j = 0; j < cols; j++)
	// // // sum += std::max(Mi[j], 0.);
	// // }
	//
	// // double sum=0;
	// // MatConstIterator_<double> it = d.begin<double>(), it_end =
	// // d.end<double>();
	// // for(; it != it_end; ++it)
	// // sum += std::max(*it, 0.);
	// //
	// // for (int i = 0; i < d.rows(); i++) {
	// // sum = d[i];
	// // }
	//
	// }

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		// prev = after;
		// after = SystemClock.elapsedRealtime();
		// System.out.println("frame time===================================== "
		// + (after - prev));

		if ((SystemClock.elapsedRealtime() - startTime) > totalperiod) {
			// destroy = true;
			finishTime = SystemClock.elapsedRealtime();
			finish();
		}

		mRgba = inputFrame.rgba();

		// // convert image to grayscale
		// Mat thrrr = new Mat();
		// Imgproc.cvtColor(mRgba, thrrr, Imgproc.COLOR_RGBA2GRAY);
		// // apply Gaussian Blur
		// Imgproc.GaussianBlur(thrrr, thrrr, new Size(9, 9), 2, 2);
		//
		// // Imgproc.
		// detectCircle(thrrr);
		// mIsColorSelected=false;

		if (mIsColorSelected) {

			// frameCOunter1++;

			mDetector.process(mRgba);
			List<MatOfPoint> contours = mDetector.getContours();
			if (contours.isEmpty()) {
				sb.append("0");
			} else {
				sb.append("1");
			}
			Log.e(TAG, "Contours count: " + contours.size());

			// Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

			Mat colorLabel = mRgba.submat(4, 68, 4, 68);
			colorLabel.setTo(mBlobColorRgba);

			Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70,
					70 + mSpectrum.cols());
			mSpectrum.copyTo(spectrumLabel);

			double maxArea = 0;
			Rect boundingRect;
			// Rect rectWithNoContour;
			// Rect rectCrop;
			// Mat imCrop;

			if (contours.size() > 0) {
				for (MatOfPoint contour : contours) {
					double area = Imgproc.contourArea(contour);
					if (area > maxArea) {
						maxArea = area;
						largestContour = contour;
					}
				}
				// // Get bounding rect of contour
				boundingRect = Imgproc.boundingRect(largestContour);
				// tempRect = boundingRect;
				// // draw enclosing rectangle (all same color, but you could
				// use
				// variable i to make them unique)
				Core.rectangle(mRgba,
						new Point(boundingRect.x, boundingRect.y), new Point(
								boundingRect.x + boundingRect.width,
								boundingRect.y + boundingRect.height),
						new Scalar(0, 255, 0, 255), 3);
				// rectCrop = new Rect(boundingRect.x, boundingRect.y,
				// boundingRect.width, boundingRect.height);
				// imCrop = new Mat(mRgba, rectCrop);
			}
			// else {
			//
			// if (tempRect == null) {
			// rectWithNoContour = new Rect(640, 360, 50, 50);
			// // Core.rectangle(mRgba, new Point(rectWithNoContour.x,
			// // rectWithNoContour.y), new Point(rectWithNoContour.x
			// // + rectWithNoContour.width, rectWithNoContour.y
			// // + rectWithNoContour.height), new Scalar(255, 0, 0,
			// // 255), 3);
			//
			// rectCrop = new Rect(rectWithNoContour.x,
			// rectWithNoContour.y, rectWithNoContour.width,
			// rectWithNoContour.height);
			// imCrop = new Mat(mRgba, rectCrop);
			// } else {
			// // Core.rectangle(mRgba, new Point(tempRect.x, tempRect.y),
			// // new Point(tempRect.x + tempRect.width, tempRect.y
			// // + tempRect.height), new Scalar(255, 0, 0,
			// // 255), 3);
			//
			// rectCrop = new Rect(tempRect.x, tempRect.y, tempRect.width,
			// tempRect.height);
			// imCrop = new Mat(mRgba, rectCrop);
			// }
			// }
			// saveImage(imCrop);
		}

		return mRgba;

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	// public void detectCircle(Mat thr) {
	// // convert image to grayscale
	// // Imgproc.cvtColor(mRgba, thr, Imgproc.COLOR_RGBA2GRAY);
	// // apply Gaussian Blur
	// // Imgproc.GaussianBlur(thr, mGray, sSize5, 2, 2);
	//
	// int iMinRadius = 0;
	// int iMaxRadius = 100;
	// int iAccumulator = 100;
	// int iCannyUpperThreshold = 200;
	// Mat detectedCircle = new Mat();
	// int radius;
	// Point pt = new Point();
	// // apply houghCircles
	// Imgproc.HoughCircles(thr, detectedCircle, Imgproc.CV_HOUGH_GRADIENT,
	// 1d, thr.rows() / 8, iCannyUpperThreshold, iAccumulator, 0, 0);
	// if (detectedCircle.cols() > 0)
	// for (int x = 0; x < Math.min(detectedCircle.cols(), 10); x++) {
	// double vCircle[] = detectedCircle.get(0, x);
	//
	// if (vCircle == null)
	// break;
	// pt.x = Math.round(vCircle[0]);
	// pt.y = Math.round(vCircle[1]);
	// radius = (int) Math.round(vCircle[2]);
	// System.out.println(vCircle[2] + "  ------------    " + radius);
	// // draw the found circle
	// Core.circle(mRgba, pt, radius, new Scalar(255, 0, 0, 255), 3);
	// }
	// }

	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
		Mat pointMatRgba = new Mat();
		Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
		Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL,
				4);

		return new Scalar(pointMatRgba.get(0, 0));
	}

	int min = 5;
	int max = 8;

	public String getBitPattern(String frameSeq) {
		// boolean bitZero = false;
		// boolean validNoofSeq = false;
		// System.out.println(frameSeq);
		int bitIncluded = -1;
		StringBuilder sb = new StringBuilder();
		char initSeq = frameSeq.charAt(0);
		int count = 0, index = 0;
		while (index < frameSeq.length()) {
			bitIncluded = -1;

			if (initSeq == frameSeq.charAt(index)) {
				count++;
				index++;
			} else {
				bitIncluded = isValidNoofSeq(count);
				if (bitIncluded > 0) {
					for (int i = 0; i < bitIncluded; i++) {
						sb.append(initSeq);
					}
				}
				initSeq = frameSeq.charAt(index);
				count = 0;
			}
			if (sb.toString().length() > 5) {
				if (sb.toString()
						.subSequence(sb.toString().length() - 3,
								sb.toString().length()).equals("000")) {
					System.out
							.println("rtyuiop[rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"
									+ sb.toString().subSequence(
											sb.toString().length() - 3,
											sb.toString().length()));
				}
			}

		}
		System.out.println("CODEGENERATED====>>>> " + sb.toString());
		return sb.toString();
	}

	public int isValidNoofSeq(int count) {
		if (count >= 5 && count <= 9) {
			return 1;
		} else if (count >= 10 && count <= 18) {
			return 2;
		} else if (count >= 19 && count <= 27) {
			return 3;
		} else {
			return -100;
		}
	}

}
