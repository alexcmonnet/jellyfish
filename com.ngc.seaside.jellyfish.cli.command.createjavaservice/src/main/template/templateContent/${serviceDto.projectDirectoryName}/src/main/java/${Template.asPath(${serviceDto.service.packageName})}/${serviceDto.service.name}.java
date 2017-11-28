package ${serviceDto.service.packageName};

import com.ngc.blocs.service.api.IServiceModule;
import com.ngc.blocs.service.event.api.IEventService;
import com.ngc.blocs.service.log.api.ILogService;
import com.ngc.seaside.service.fault.api.IFaultManagementService;
import com.ngc.blocs.service.thread.api.IThreadService;
import com.ngc.seaside.service.fault.api.ServiceFaultException;
#if (!$baseServiceDto.correlationMethods.isEmpty())
import com.ngc.seaside.service.correlation.api.ICorrelationService;
import com.ngc.seaside.service.correlation.api.ILocalCorrelationEvent;
#end
#foreach ($i in $serviceDto.service.imports)
import ${i};
#end
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(service = {#if($serviceDto.service.implementedInterface)${serviceDto.service.implementedInterface.name}.class, #{end}IServiceModule.class}, immediate = true)
public class ${serviceDto.service.name}#if($serviceDto.service.baseClass) extends ${serviceDto.service.baseClass.name}#elseif($serviceDto.service.implementedInterface) implements ${serviceDto.service.implementedInterface.name}#end {

#foreach ($method in $baseServiceDto.basicPubSubMethods)
   @Override
   public ${method.outputType} ${method.serviceName}(${method.inputType} input) throws ServiceFaultException {
      throw new UnsupportedOperationException("not implemented");
   }
#end
#foreach ($method in $baseServiceDto.basicSinkMethods)
   @Override
   public void ${method.serviceName}(${method.inputType} input) throws ServiceFaultException {
      // TODO: implement this
      throw new UnsupportedOperationException("not implemented");  
   }

#end
#foreach ($method in $baseServiceDto.correlationMethods)
   @Override
   public ${method.outputType} ${method.serviceName}(
#foreach ($input in $method.inputs)
      ${input.type} ${input.inputArgumentString},
#end
      ILocalCorrelationEvent<${method.correlationType}> correlationEvent) throws ServiceFaultException {
         // TODO: implement this
         throw new UnsupportedOperationException("not implemented");
   }

#end
#foreach ($scenario in $baseServiceDto.complexScenarios)
   @Override
   void ${scenario.serviceName}(
#foreach ($input in $scenario.inputs)
#set ($lastParam = $velocityCount == $scenario.inputs.size() && $scenario.outputs.isEmpty())
      BlockingQueue<${input.type}> input${velocityCount}Queue#if ($lastParam));#{else},#end
#end
#foreach ($output in $scenario.outputs)
#set ($lastParam = $velocityCount == $scenario.outputs.size())
      Consumer<${output.type}> output${velocityCount}Consumer#if ($lastParam));#{else},#end
#end

#end


   @Activate
   public void activate() {
      super.activate();
   }

   @Deactivate
   public void deactivate() {
      super.deactivate();
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, unbind = "removeLogService")
   public void setLogService(ILogService ref) {
      super.setLogService(ref);
   }

   @Override
   public void removeLogService(ILogService ref) {
      super.removeLogService(ref);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, unbind = "removeEventService")
   public void setEventService(IEventService ref) {
      super.setEventService(ref);
   }

   @Override
   public void removeEventService(IEventService ref) {
      super.removeEventService(ref);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, unbind = "removeFaultManagementService")
   public void setFaultManagementService(IFaultManagementService ref) {
      super.setFaultManagementService(ref);
   }

   @Override
   public void removeFaultManagementService(IFaultManagementService ref) {
      super.removeFaultManagementService(ref);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, unbind = "removeThreadService")
   public void setThreadService(IThreadService ref) {
      super.setThreadService(ref);
   }

   @Override
   public void removeThreadService(IThreadService ref) {
      super.removeThreadService(ref);
   }
   
#if (!$baseServiceDto.correlationMethods.isEmpty()) 
   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, unbind = "removeCorrelationService")
   public void setCorrelationService(ICorrelationService ref) {
      this.correlationService = ref;
   }
      
   @Override
   public void removeCorrelationService(ICorrelationService ref) {
      setCorrelationService(null);
   }

#end
}
