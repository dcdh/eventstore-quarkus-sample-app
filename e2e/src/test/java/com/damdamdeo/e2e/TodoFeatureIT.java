package com.damdamdeo.e2e;

import cucumber.runtime.arquillian.CukeSpace;
import cucumber.runtime.arquillian.api.Features;
import cucumber.runtime.arquillian.api.Glues;
import cucumber.runtime.arquillian.api.Tags;
import org.junit.runner.RunWith;

@Features("com/damdamdeo/e2e/Todo.feature")
@RunWith(CukeSpace.class)
@Glues({TodoFeatureStepsIT.class})
@Tags("~@Ignore")
public class TodoFeatureIT {

}
