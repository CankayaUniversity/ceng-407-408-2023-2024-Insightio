## 1. Introduction                       

 
## 2. General Description

 
## 3. Specific Requirements

	Insightio is a comprehensive platform designed to address the counting and tracking needs of businesses, offering solutions for both crowd and object monitoring. This section outlines the specific requirements that govern the functionality and user interface of the Insightio platform. The following subsections detail the interface requirements and provide a comprehensive description of the functional and non-functional requirements, each formulated to be precise, testable, and directly aligned with the project's objectives.

### 3.1. Interface Requirements

	The interface of Insightio is designed to provide an intuitive and user-friendly experience for businesses seeking crowd and object counting solutions. The specifications for the user interface, such as account setup, camera configuration, and statistical data access, are described in this section.

#### 3.1.1. Account Creation

•	The system shall provide a user registration page with fields for username, email, and password.
•	System must allow passwords to be at least 8 characters long and to include at least one uppercase, one lowercase, one special character and one number.
•	Upon successful registration, the system shall send a confirmation email to the provided email address for account verification.
•	The user interface shall include a login page with fields for username and password.
•	Users shall have the option to reset their password via a 'Forgot Password' link on the login page.

#### 3.1.2. License Procurement

•	After successful login, users shall be able to access a section for procuring a commercial license.
•	The license procurement process shall guide users through selecting the appropriate licensing options and making the necessary payments securely.

#### 3.1.3. Camera Setup

•	The system shall provide a dashboard for users to add and configure cameras.
•	Users shall be able to specify the class of objects they intend to track for each camera.
•	Optionally, users may define polygonal zones on camera images to restrict counting to specific areas.

#### 3.1.4. Statistics Access

•	Upon successful camera configuration, users shall have access to statistical information related to the tracked subject such as average population, rush and non-peek hours.
•	The platform shall offer an intuitive interface for users to view and interact with the generated charts and plots.

### 3.2. Detailed Description of Functional Requirements

	The particular functional requirements that specify how the Insightio platform behaves are described in this section. Each functional requirement listed here is designed with the ideas of precision and testability in mind.

#### 3.2.1. User Authentication

•	The system shall authenticate users based on their provided username and password.
•	System must log every login attempt for observability and reliability.
•	Failed login attempts shall trigger appropriate error messages for incorrect credentials.
•	Successful authentication shall grant users access to their personalized dashboard.

#### 3.2.2. Email Verification

•	Upon successful registration, the system shall send a verification email to the provided address.
•	Users must confirm their registration by clicking the verification link within the email.

#### 3.2.3. License Management

•	Users shall be able to select and purchase a suitable commercial license from the available options.
•	The system shall generate and associate a unique license key with each purchased license.

#### 3.2.4. Camera Configuration

•	Users shall be able to add new cameras to their account, providing necessary details such as camera name and IP address.
•	For each camera, users shall be able to specify the class of objects they wish to track.

#### 3.2.5. Zone Definition

•	Optionally, users may define polygonal zones on camera images to restrict counting to particular areas of interest.
•	The system shall provide an intuitive interface for users to draw and modify these zones.

#### 3.2.6. Object Counting and Tracking

•	Once cameras are configured, a server-side script shall execute to perform object counting and tracking.
•	The script shall run concurrently for each active camera, relaying gathered information to the server.

#### 3.2.7. Data Processing

•	The server shall process the received data from active cameras for transmission to the platform's frontend.
•	Data processing shall include aggregation and formatting of object count information.

#### 3.2.8. Statistical Visualization

•	The platform shall generate charts and plots based on the received object count data.
•	Users shall have the ability to select specific time periods (day, week, month, year, etc.) for visualization.

#### 3.2.9. User Password Reset

•	The system shall allow users to reset their password by providing their registered email address.
•	A password reset link shall be sent to the user's email for security verification.

#### 3.2.10. Error Handling and Logging

•	The platform shall implement robust error handling mechanisms to gracefully manage unexpected events.
•	Detailed logs shall be maintained to track system behavior and aid in debugging processes.

### 3.3. Detailed Description of Non-Functional Requirements


## 4. UML - Use Cases

	Use cases in Insightio represent essential functionalities that cater to the diverse needs of its users. Each use case encapsulates a specific interaction scenario within the platform, contributing to its overall functionality.

### 4.1. INS-UC-001: Dashboard Integration
 
### 4.2. INS-UC-002: Server-Client Communication
 
### 4.3. INS-UC-003: AI-Driven Object Tracking
 
### 4.4. INS-UC-004: User Account Creation

### 4.5. INS-UC-005: Camera Configuration
 
Use Case Name: INS-UC-005

Related Use Cases: -

Actors: User, System Server, Target Counter Software

Description: To initiate the recording of target counts, users must configure a camera through the platform interface. This setup involves assigning a name to the camera, specifying the URL of the camera device, and choosing target class(es) to inform the counting software about what to track. Optionally, at this stage, users can also estab-lish capture zones known as polygon zones, concentrating counting efforts on specif-ic regions within the camera feed. Users also have the option to choose or exclude statistical charts displaying the desired data from the captured camera feed. After completing the configuration, the camera settings are transmitted to the server for distribution to the counting software.

Preconditions:

•	User logged into the system.
•	User navigated to the camera configuration page.

Postconditions:

•	A new camera is accepted as a data source or a an existing camera is given new targets.
•	Alternatively, if provided device URL is invalid, camera configuration inser-tion or update fails.

 
Main Flow for INS-UC-005: 

1.	User specifies a camera name and provides the device URL.
2.	User chooses target class(es) for tracking.
3.	User optionally configures capture zones within the camera feed.
4.	User optionally customizes statistical charts for desired data display.
5.	User reviews and confirms the camera settings.
6.	User transmits the configured settings to the server.
7.	The server distributes the settings to the counting software.
8.	Server confirms validity of device URL.
9.	Counting software initiates recording based on the configured settings.

Alternative Flow for INS-UC-005:

      8.a.  If the server cannot confirm the provided device URL belongs to a valid camera device, server does not forward the settings to the tracking software and re-turns an error to the user interface which the interface then displays to the user.
 
## 5. DFDs Overview

	The Data Flow Diagrams (DFDs) for Insightio offer a concise portrayal of the system's data interactions. At Level 0, key components of the ecosystem are highlighted. These entities form the foundational structure, showcasing the primary data flows between users, cameras, and the Insightio System. The DFDs collectively illustrate the initial data pathways and entities that shape the broader Insightio data ecosystem.

### 5.1. Level 0
                                                                                                                  
	In Level 0, the Data Flow Diagram (DFD) accentuates pivotal external factors and entities integral to the initial data exchanges. This delineation encapsulates the data transactions between the Insightio System and users, incorporating the camera devices they utilize. As illustrated in the diagram, Insightio assumes the role of an intermediary, managing and interpreting camera feeds to enhance user convenience.

 

### 5.2. Level 1

	Transitioning to Level 1, a more comprehensive exploration unveils specific processes and data stores, offering a detailed insight into the operations performed by the Insightio System. The diagram categorizes these operations into four main components: User Login/Signup, encompassing processes related to user account management; Camera Configuration and Target Selection, involving procedures for establishing camera connections and specifying demographic data targets; Data Processing, representing all processes related to extracting target demographics from camera feed frames; and Demographics Visualizations, consolidating processes designed to present demographic data to users through charts and plots.

	This meticulous breakdown enriches understanding of the intricate data movements and processes within the Insightio platform, providing clarity from user interactions to data processing and visualization.

## 6. Conclusion

	In summary, Insightio is a platform designed to meet businesses' crowd and object counting and tracking needs. This Software Requirements Specification (SRS) document provides an objective overview of the system's purpose, scope, and functional details.

	The platform streamlines critical operations such user account creation, commercial license acquisition, camera setting, and statistical data access. It caters to a variety of user roles, including business owners, operations managers, security personnel, data analysts, general users, and technical support staff, with features tailored to their specific needs and technical competence.

	The functional requirements emphasize clear and testable specifications and cover critical areas such as user authentication, license administration, camera setup, real-time tracking, and data processing. Security, performance, fault tolerance, maintainability, internationalization, server system capacity, connectivity, usability, documentation, and data accuracy are examples of non-functional requirements.

	The use cases present practical situations exhibiting user interactions with the platform, hence assisting in a thorough knowledge of system functionality

	In conclusion, Insightio is a comprehensive solution, and the requirements mentioned serve as a guide for the development process, ensuring the delivery of a secure, stable, and user-friendly platform. 
