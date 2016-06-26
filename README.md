# AndroidShapeDetection

AndroidShapeDetection is an andoid application to visualize edges and shape 
detection on images using opencv.

The application lets the user select an image and then performs a series of
opencv based image processing. Currently, it does the following.
* convert to gray scale
* blur
* detect edges using Canny Edge detector
* Identify & classfiy contours

The processed images are presented to the user as view pages/tabs. The app also
provides options to tune/set certain parameters used for image processing. The
following settings options are currently available
* Blur image  
    -Kernel size
* Canny Edge Detection  
    -Use OTSU threshold  
    -Max Threshold  
    -Min Threshold  
* Contour Detection  
    -Type

## Screenshots

![Raw Image](https://github.com/nitheeshkl/AndroidShapeDetection/blob/master/screenshots/raw_img.png)

![Gray scale & Blur](https://github.com/nitheeshkl/AndroidShapeDetection/blob/master/screenshots/gray_scale_blur_img.png)

![Edges detected](https://github.com/nitheeshkl/AndroidShapeDetection/blob/master/screenshots/edges.png)

![Contours detected](https://github.com/nitheeshkl/AndroidShapeDetection/blob/master/screenshots/contours.png)

![Settings](https://github.com/nitheeshkl/AndroidShapeDetection/blob/master/screenshots/settings.png)

