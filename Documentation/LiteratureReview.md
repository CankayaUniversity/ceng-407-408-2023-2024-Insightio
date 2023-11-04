# 1. Literature Review

This file includes the text for the literature review paper that is required as per CENG 407 project development and documentation guidelines. Each section of the paper is committed by their respective authors.

References for citations follow the guidelines set forth in [APA 7](https://apastyle.apa.org/instructional-aids/reference-guide.pdf).

Authors:

[Eda Nur Altunok](https://github.com/edanuraltnk)<br>
[Ekin Nalçacı](https://github.com/ekinnalcaci)<br>
[Günalp Güngör](https://github.com/gunalpgungor)<br>
[Utku Özbek](https://github.com/revtheundead)<br>
[Zeynep Polat](https://github.com/zeyneppolat01)<br>

## Özet

## Abstract

## 1. Introduction

In today’s global landscape, grappling with the complexities of managing human presence has become increasingly challenging due to the ever-expanding population. Businesses, public spaces, and communities seeking to efficiently navigate and analyze crowded environments require robust tools for data acquisition and manipulation. The global market for people counting systems reached a valuation of 969.9 million USD in 2021, with a projected annual growth rate of 12.2% from 2022 to 2030 (People Counting System Market Size, Share & Trends Analysis Report by End-use (BFSI, Corporate), by Offering (Hardware, Software), by Mounting Platform (Ceiling, Wall), by Type, by Technology, and Segment Forecasts, 2022 - 2030, n.d.).

Importantly, people counting methods are essential in many industries, retail being one of the best examples. The transition from manual counting to sophisticated computer solutions has been crucial for retail organizations seeking to modernize operations and improve consumer experiences. According to Chakraborty (2023), these systems provide real-time analysis, identify high-traffic areas, optimize staffing, examine consumer behavior, and facilitate forecasting.

Real-time tracking also extends its reach to emergency evacuations, where human-centered sensing proves invaluable for monitoring crowd behavior in real-time, ultimately bolstering incident management strategies (Crowd Models for Emergency Evacuation: A Review Targeting Human-Centered Sensing, 2013).

These statistics underscore the profound impact of people counting technology across diverse industries. Evidently, businesses and organizations are increasingly embracing this technology to enhance operational efficiency, elevate security measures, and foster data-informed decision-making processes.


## 2. Main Findings

The literature review findings are bifurcated into two distinct parts. The first part scrutinizes the challenges entailed in accurately discerning individuals within crowded spaces. This encompasses considerations such as variations in lighting, weather, and image quality, in addition to complexities arising from overlapping individuals and occluded areas.

The following section then explores the development of computer vision-based approaches. The noteworthy advancements in computational approaches and their usefulness in crowd counting are emphasized. This includes talking about methods such as density regression and dense detection and how they can be used to improve the accuracy and efficiency of this field. All of these studies provide an extensive understanding of the basic concepts and recent developments in crowd counting.

### 2.1.  Challenges and Difficulties In Human Detection and Counting

### 2.2. Computer Vision Based Approaches To Crowd Counting

Crowd counting has witnessed a notable evolution in computer vision algorithms. Initially, density regression techniques were employed, estimating crowd density maps to infer counts (Raghavachari et al., 2015). However, this approach faced limitations, particularly in scenarios with dense crowds (Sam et al., 2020). To address this, recent advancements have shifted towards dense detection methods, which present more practical and accurate solutions (Sam et al., 2020). Notably, deep learning models, including Convolutional Neural Networks (CNNs) like VGG and ResNet, have played a crucial role in this transition.

According to Bhangale et al. (2020), these models excel in feature extraction and pattern recognition, rendering them well-suited for crowd counting tasks. Moreover, they have been adeptly adapted to handle the intricacies of crowd analysis, effectively capturing complex spatial arrangements and density variations (Sam et al., 2020; Sjöberg & Hyberg, 2023; Hussain, 2023). This adaptability showcases the versatility of CNNs in addressing the challenges posed by dynamic crowd environments.

In addition to CNNs, YOLO (You Only Look Once) models have emerged as powerful tools in object detection, including humans, and have been particularly effective in crowd counting tasks (Hussain, 2023). These models utilize a single neural network to simultaneously predict bounding boxes and class probabilities for multiple objects in a single pass. This makes them well-suited for real-time processing and applications like object detection and tracking in video streams. YOLO models, including the latest YOLO-v8 release, have demonstrated high-classification performance and fast detection capabilities, making them increasingly relevant in industrial settings (Hussain, 2023).

Additionally, techniques like multi-scale analysis have proved indispensable, given that crowd images often encompass individuals of varying sizes (Huang & Chung, 2004; Hussain, 2023). Methods such as image pyramid decomposition and feature pyramids facilitate the analysis of images at different resolutions. This approach ensures that individuals are detected and counted irrespective of their scale, thereby bolstering the overall robustness of the counting system (Huang & Chung, 2004; Hussain, 2023). This emphasis on multi-scale analysis underscores its pivotal role in accurately estimating crowd counts, especially in scenarios with diverse crowd compositions.

Furthermore, the integration of detection and tracking systems has demonstrated remarkable efficacy in dynamic environments. As suggested by Huang and Chung (2004) and Sjöberg and Hyberg (2023), detection algorithms identify individuals, while tracking algorithms ensure object continuity across frames. This synergistic approach significantly enhances crowd counting reliability, particularly in situations characterized by occlusions and dynamic movement (Sjöberg & Hyberg, 2023; Hussain, 2023). The fusion of detection and tracking mechanisms addresses challenges associated with the inherent movement and occlusions within crowd scenes.

Some other methods have also significantly contributed to the evolution of crowd counting algorithms. For instance, CSRNet, a CNN tailored for congested scene recognition, has emerged as a prominent tool in crowd counting applications (Bhangale et al., 2020). This model employs a multi-column architecture in conjunction with top-down modulation, allowing for the precise detection and localization of individuals within densely populated scenes. Notably, CSRNet's proficiency in generating accurate crowd density maps from point annotations underscores its robustness in crowd counting scenarios.

In addition to CSRNet, the integration of advanced image descriptors like Perspective Invariant Histograms of Oriented Gradients (HOGp) has revolutionized crowd counting, eliminating the need for camera calibration (Reis, 2014). This innovative technique not only addresses privacy concerns but also proves particularly adept in systems with limited resources. Consequently, it stands as a valuable tool in urban planning and crowd control applications. By emphasizing these image descriptors, this approach offers an alternative avenue to achieve accurate crowd counting.

Furthermore, the application of Hidden Markov Models (HMM) shows promise in recognizing various human dynamics, including walking and sitting (Huang & Chung, 2004). HMMs provide a robust framework for understanding temporal dependencies within crowd behavior, introducing an additional layer of sophistication to crowd counting algorithms (Huang & Chung, 2004). The fusion of HMMs with other detection and tracking methodologies presents a holistic approach to crowd analysis.

In summary, the landscape of crowd counting has witnessed a transition from traditional density regression techniques to the adoption of dense detection strategies. While deep learning models like VGG, ResNet, and YOLO have played pivotal roles, it is imperative to recognize the substantial contributions of other noteworthy models like CSRNet, innovative techniques like HOGp descriptors, and the integration of HMMs. These diverse methods collectively underscore the versatility and adaptability of computer vision in achieving accurate crowd estimates across various domains, ranging from smart building management to urban planning and public safety.

## 3. Evaluating Model Accuracies

## 4. Conclusion

## References

Bhangale, U., Patil, S., Vishwanath, V., Thakker, P., Bansode, A., & Navandhar, D. (2020). Near Real-time Crowd Counting using Deep Learning Approach. Procedia Computer Science, 171, 770–779. https://doi.org/10.1016/j.procs.2020.04.084

Crowd Models for Emergency Evacuation: A review Targeting Human-Centered Sensing. (2013, January 1). IEEE Conference Publication | IEEE Xplore. https://ieeexplore.ieee.org/abstract/document/6479853

Chakraborty, A. (2023). How People Counting Systems Enhance Customer Experience In Retail Stores. LinkedIn. https://www.linkedin.com/pulse/how-people-counting-systems-enhance-customer-retail-chakraborty/

Huang, C., & Chung, C. (2004). A Real-Time Model-Based human motion tracking and analysis for Human-Computer Interface systems. EURASIP Journal on Advances in Signal Processing, 2004(11). https://doi.org/10.1155/s1110865704401206

Hussain, M. (2023). YOLO-v1 to YOLO-v8, the Rise of YOLO and Its Complementary Nature toward Digital Manufacturing and Industrial Defect Detection. Machines, 11(7), 677. https://doi.org/10.3390/machines11070677

People Counting System Market Size, Share & Trends Analysis Report By End-use (BFSI, Corporate), By Offering (Hardware, Software), By Mounting Platform (Ceiling, Wall), By Type, By Technology, And Segment Forecasts, 2022 - 2030. (n.d.). https://www.grandviewresearch.com/industry-analysis/people-counting-system-market-report

Raghavachari, C., Aparna, V., Chithira, S., & Balasubramanian, V. (2015). A Comparative Study of Vision Based Human Detection Techniques in People Counting Applications. Procedia Computer Science, 58, 461–469. https://doi.org/10.1016/j.procs.2015.08.064

Rahman, M. A. (2017). Computer Vision Based Human Detection. International Journal of Engineering and Information Systems (IJEAIS), 1(5), 62-85. https://hal.science/hal 01571292/document 

Reis J. (2014). *Image descriptors for counting people with uncalibrated cameras* [Master Thesis, University of Porto]. U. Porto. https://paginas.fe.up.pt/~ee08266/thesis.html

Sam, D. B., Peri, S. V., Sundararaman, M. N., Kamath, A., & Babu, R. V. (2020). Locate, size and count: Accurately resolving people in dense crowds via detection. IEEE Transactions on Pattern Analysis and Machine Intelligence, 1. https://doi.org/10.1109/tpami.2020.2974830

Sjöberg, A. & Hyberg, J. (2023). *Investigation regarding the Performance of YOLOv8 in Pedestrian Detection* [Bachelor's Thesis, KTH Royal Institute Of Technology]. Diva Portal. https://kth.diva-portal.org/smash/get/diva2:1778368/FULLTEXT01.pdf