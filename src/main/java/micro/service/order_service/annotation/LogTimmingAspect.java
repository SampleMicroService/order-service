package micro.service.order_service.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogTimmingAspect {
	
	@Around("@annotation(LogTimming)")
	public Object printTimming(ProceedingJoinPoint point) throws Throwable {
		long start = System.currentTimeMillis();
		Object proceed = point.proceed();
		long end = System.currentTimeMillis();
		long timming = end - start;
		System.out.println(point.getSignature() + " has exicuted in "+timming+" ms");
		return proceed;
	}
}
