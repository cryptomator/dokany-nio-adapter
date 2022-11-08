package org.cryptomator.frontend.dokany;

import com.dokany.java.Dokany;
import org.cryptomator.integrations.mount.MountService;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EnabledIfDokanyInstalledCondition implements ExecutionCondition {

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if(Dokany.isInstalled()){
			return ConditionEvaluationResult.enabled("Found Dokany driver, execute dokany mirror tests.");
		} else {
			return ConditionEvaluationResult.disabled("Unable to locate Dokany driver. Disabled mirror test.");
		}
	}
}
