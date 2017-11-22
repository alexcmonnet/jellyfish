package ${dto.abstractClass.packageName};

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.ngc.blocs.api.IContext;
import com.ngc.blocs.api.IStatus;
import com.ngc.blocs.service.api.IServiceModule;
import com.ngc.blocs.service.api.ServiceStatus;
import com.ngc.blocs.service.event.api.IEventService;
import com.ngc.blocs.service.event.api.Subscriber;
import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.service.fault.api.IFaultManagementService;
import com.ngc.seaside.service.fault.api.ServiceFaultException;
import com.ngc.blocs.service.thread.api.IThreadService;
#foreach ($i in $dto.abstractClass.imports)
import ${i};
#end

public abstract class ${dto.abstractClass.name}
   implements IServiceModule, ${dto.interface.name} {

   public final static String NAME = "service:${dto.model.fullyQualifiedName}";

   protected IContext<?> context;

   protected ServiceStatus status = ServiceStatus.DEACTIVATED;

   protected IEventService eventService;

   protected ILogService logService;

   protected IFaultManagementService faultManagementService;
   
   protected IThreadService threadService;

#if (!$dto.correlationMethods.isEmpty())
   protected ICorrelationService correlationService;

   protected Map<ICorrelationTrigger, Collection<Consumer<ICorrelationStatus<?>>>> triggers = new ConcurrentHashMap<>();

#end
#if (!$dto.complexScenarios.isEmpty())
   private Map<Class<?>, Collection<Queue<?>>> queues = new ConcurrentHashMap<>();

   private Map<String, ISubmittedLongLivingTask> threads = new ConcurrentHashMap<>();

#end
############################### Receive methods ###############################
#foreach($method in $dto.receiveMethods)
   @Subscriber(${method.topic})
   public void ${method.name}(IEvent<${method.eventType}> event) {
      ${method.eventType} source = event.getSource();

#foreach($scenario in $method.basicScenarios)
      ${scenario}(source);
#end
#if ($method.hasCorrelations())
      correlationService.correlate(source)
         .stream()
         .filter(ICorrelationStatus::isCorrelationComplete)
         .forEach(status -> {
            triggers.get(status.getTrigger()).forEach(consumer -> consumer.accept(status))
         });
#end 
#if ($method.hasComplexScenarios())
      queues.getOrDefault(${method.eventType}.class, Collections.emptyList()).forEach(queue -> queue.add(source));
#end
   }

#end
############################### Publish methods ###############################
#foreach($method in $dto.publishMethods)
   private void ${method.name}(${method.type}) value) {
      Preconditions.checkNotNull(value, "${method.type} value may not be null!");
      eventService.publish(value, ${method.topic});
   }

#end
#################### Basic 1-input 1-output pubsub methods ####################
#foreach($method in $dto.basicPubSubMethods)
   private void ${method.name}(${method.inputType} input) {
      ${method.outputType} output;
      try {
         output = ${method.serviceMethod}(input);
      } catch(ServiceFaultException fault) {
         // TODO
         return;
      }
#foreach($correlation in $method.inputOutputCorrelations)
      output.${correlation.setterSnippet}(${correlation.getterSnippet});
#end
      logService.info(getClass(), "TODO", input, output);
      ${method.publishMethod}(output);
   }
   
#end
##################### Basic 1-input 0-output sink methods #####################
#foreach($method in $dto.basicSinkMethods)
   private void ${method.name}(${method.inputType} input) {
      try {
         ${method.serviceMethod}(input);
      } catch(ServiceFaultException fault) {
         // TODO
         return;
      }
      logService.info(getClass(), "TODO", input);
   }

#end
########## Multi-input 1-output methods with input-input correlation ##########
#foreach($method in $dto.correlationMethods)
   private void ${method.name}(ICorrelationStatus<?> status) {
      updateRequestWithCorrelation(status.getEvent());
      try {
         @SuppressWarnings("unchecked")
         ${method.outputType} output = ${method.serviceName}(
#foreach($input in $method.inputs)
               status.getData(${input.type}.class),
#end
               (ILocalCorrelationEvent<${method.correlationType}>) status.getEvent());
#foreach($correlation in $method.inputOutputCorrelations)
         output.${correlation.setterSnippet}(status.getData(${correlation.inputType}.class).${correlation.getterSnippet});
#end
         logService.info(getClass(), "ELK - Scenario: ${method.scenarioName}; Input: ${method.inputLogFormat}; Output: %s;", 
#foreach($input in $method.inputs)
            status.getData(${input.type}.class).toString(),
#end
            output.toString());
         ${method.publishMethod}(output);
      } catch (ServiceFaultException fault) {
         logService.error(getClass(),
                          "Invocation of '%s.${method.serviceName}' generated fault, dispatching to fault management service.",
                          getClass().getName());
         faultManagementService.handleFault(fault);
      } finally {
         clearCorrelationFromRequest();
      }
   }

#end
############################ Trigger Registrations ############################
#foreach($method in $dto.triggerRegistrationMethods)
   private void ${method.name}() {
      ICorrelationTrigger<${method.triggerType}> trigger = correlationService.newTrigger(${method.triggerType})
#foreach($eventDto in $method.eventProducers)
            .addEventIdProducer(${eventDto.type}.class, a -> a.${eventDto.getterSnippet})
#end
#foreach($completenessDto in $method.completionStatements)
            .addCompletenessCondition(${completenessDto.input1Type}.class, ${completenessDto.input2Type}.class, (a, b) -> 
               a.${completenessDto.input1GetterSnippet}.equals(b.${completenessDto.input2GetterSnippet}))
#end
            .register();
#foreach($input in $method.inputs)
      triggers.get(trigger).add(this::${input.correlationMethod});
#end
   }

#end
############ Multi-input multi-output methods without correlation #############
#foreach($scenario in $dto.complexScenarios)
#set ($serviceArguments = "")
#foreach($input in $scenario.inputs)
#set ($serviceArguments = "${serviceArguments}, input${velocityCount}Queue")
#end
#foreach($output in $scenario.outputs)
#set ($serviceArguments = "${serviceArguments}, ${dto.abstractClass.name}.this::${output.publishMethod}")
#end
#set ($serviceArguments = $serviceArguments.substring(2))
   private void ${scenario.startMethod}() {
      threads.put("${scenario.name}", threadService.executeLongLivingTask("${scenario.name}", () {
#foreach($input in $scenario.inputs)
         final BlockingQueue<${input.type}> input${velocityCount}Queue = new LinkedBlockingQueue<>();
#end
#foreach($input in $scenario.inputs)
         queues.putIfAbsent(${input.type}.getClass(), __ -> Collections.newSetFromMap(new IdentityHashMap<>())).add(input${velocityCount}Queue);
#end
         try {
            ${scenario.serviceMethod}(${serviceArguments});
         } finally {
            threads.remove("${scenario.name}");
#foreach($input in $scenario.inputs)
            queues.getOrDefault(${input.type}.class, Collections.emptySet()).remove(input${velocityCount}Queue);
#end
         }
      }));
   }
#end
################################## Activate ###################################
protected void activate() {
#foreach($method in $dto.triggerRegistrationMethods)   
   ${method.name}();
#end
#foreach($scenario in $dto.complexScenarios)
   ${scenario.startMethod}();
#end
   eventService.addSubscriber(this);
   setStatus(ServiceStatus.ACTIVATED);
   logService.info(getClass(), "activated");
}

################################# Deactivate ##################################
   protected void deactivate() {
      eventService.removeSubscriber(this);
      triggers.keySet().forEach(ICorrelationTrigger::unregister);
      triggers.clear();
      setStatus(ServiceStatus.DEACTIVATED);
      logService.info(getClass(), "deactivated");
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public IContext<?> getContext() {
      return context;
   }

   @Override
   public void setContext(@SuppressWarnings("rawtypes") IContext iContext) {
      this.context = iContext;
   }

   @Override
   public IStatus<ServiceStatus> getStatus() {
      return status;
   }

   @Override
   public boolean setStatus(IStatus<ServiceStatus> iStatus) {
      Preconditions.checkNotNull(iStatus, "iStatus may not be null!");
      this.status = iStatus.getStatus();
      return true;
   }

   public void setLogService(ILogService ref) {
      this.logService = ref;
   }

   public void removeLogService(ILogService ref) {
      setLogService(null);
   }

   public void setEventService(IEventService ref) {
      this.eventService = ref;
   }

   public void removeEventService(IEventService ref) {
      setEventService(null);
   }
   
#if (!$dto.correlationMethods.isEmpty())   
   public void setCorrelationService(ICorrelationService ref) {
      this.correlationService = ref;
   }
   
   public void removeCorrelationService(ICorrelationService ref) {
      setCorrelationService(null);
   }

#end
   public void setFaultManagementService(IFaultManagementService ref) {
      this.faultManagementService = ref;
   }

   public void removeFaultManagementService(IFaultManagementService ref) {
      setFaultManagementService(null);
   }

   public void setThreadService(IThreadService ref) {
      this.threadService = ref;
   }

   public void removeThreadService(IThreadService ref) {
      setThreadService(null);
   }

   @SuppressWarnings("unchecked")
   private void updateRequestWithCorrelation(ILocalCorrelationEvent<?> event) {
      IRequest request = Requests.getCurrentRequest();
      if (request instanceof ServiceRequest) {
         ((ServiceRequest) request).setLocalCorrelationEvent(event);
      }
   }

   private void clearCorrelationFromRequest() {
      IRequest request = Requests.getCurrentRequest();
      if (request instanceof ServiceRequest) {
         ((ServiceRequest) request).clearLocalCorrelationEvent();
      }
   }
}
