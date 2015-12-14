# pz_serviceregistry
Repository containing the framework/implementation of the Piazza ServiceRegistry.  The ServiceRegistry controls the registration, management and execution of service instances.    The ServiceRegistry serves as a central location for Piazza users to register, discover and utilize services.  Service instances that are registered within Piazza expose a remote API (HTTP/REST) at a given location (URL containing host, port, servicename, etc.). A service instance is a web service which is external to the Piazza framework, but is registered within Piazza so it can be discovered and utilized by Piazza users.  Service developers can develop and register services using the Piazza JSON API.   The ServiceRegistry contains a ServiceController which handles client requests and controls the execution of services. 

