package org.cryptomator.frontend.dokany;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EnabledIfDokanyInstalledCondition implements ExecutionCondition {

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if(MountFactory.isApplicable()){
			return ConditionEvaluationResult.enabled("Found Dokany driver, execute dokany mirror tests.");
		} else {
			return ConditionEvaluationResult.disabled("Unable to locate Dokany driver. Disabled mirror test.");
		}
	}
}
