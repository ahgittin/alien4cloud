package alien4cloud.paas.plan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import alien4cloud.component.repository.exception.CSARVersionAlreadyExistsException;
import alien4cloud.paas.model.PaaSNodeTemplate;
import alien4cloud.paas.plan.OperationCallActivity;
import alien4cloud.paas.plan.PaaSPlanGenerator;
import alien4cloud.paas.plan.ParallelGateway;
import alien4cloud.paas.plan.ParallelJoinStateGateway;
import alien4cloud.paas.plan.StartEvent;
import alien4cloud.paas.plan.StateUpdateEvent;
import alien4cloud.paas.plan.TopologyTreeBuilderService;
import alien4cloud.paas.plan.WorkflowStep;
import alien4cloud.tosca.container.archive.CsarUploadService;
import alien4cloud.tosca.container.exception.CSARParsingException;
import alien4cloud.tosca.container.exception.CSARValidationException;
import alien4cloud.tosca.container.model.topology.Topology;
import alien4cloud.utils.FileUtil;
import alien4cloud.utils.YamlParserUtil;

/**
 * Test for plan generation.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application-context-test.xml")
@Slf4j
public class PaaSPlanGeneratorTest {
    @Resource
    private CsarUploadService csarUploadService;
    @Resource
    private TopologyTreeBuilderService topologyTreeBuilderService;

    private static final String CSAR_SOURCE_PATH = "src/test/resources/alien/paas/plan/csars/";
    private static final String TOPOLOGIES_PATH = "src/test/resources/alien/paas/plan/topologies/";

    @AfterClass
    @BeforeClass
    public static void cleanup() throws IOException {
        FileUtil.delete(Paths.get("target/alien"));
    }

    private void initialize() throws CSARParsingException, CSARVersionAlreadyExistsException, CSARValidationException, IOException {
        log.info("Initializing ALIEN repository.");

        Path inputPath = Paths.get(CSAR_SOURCE_PATH + "tosca-base-types-1.0");
        System.out.println(inputPath.toAbsolutePath());
        Path zipPath = Files.createTempFile("csar", ".zip");
        FileUtil.zip(inputPath, zipPath);
        csarUploadService.uploadCsar(zipPath);

        inputPath = Paths.get(CSAR_SOURCE_PATH + "tomcat-types-0.1");
        zipPath = Files.createTempFile("csar", ".zip");
        FileUtil.zip(inputPath, zipPath);
        csarUploadService.uploadCsar(zipPath);

        inputPath = Paths.get(CSAR_SOURCE_PATH + "postgresql-types-0.1");
        zipPath = Files.createTempFile("csar", ".zip");
        FileUtil.zip(inputPath, zipPath);
        csarUploadService.uploadCsar(zipPath);

        inputPath = Paths.get(CSAR_SOURCE_PATH + "apache-lb-types-0.1");
        zipPath = Files.createTempFile("csar", ".zip");
        FileUtil.zip(inputPath, zipPath);
        csarUploadService.uploadCsar(zipPath);

        log.info("Types have been added to the repository.");
    }

    @Test
    public void testBuildPlan() throws Exception {
        // initialize repository with indexed node types.
        initialize();

        // load a topology
        Topology topology = YamlParserUtil.parseFromUTF8File(Paths.get(TOPOLOGIES_PATH + "sample-application.yml"), Topology.class);
        topology.setId(UUID.randomUUID().toString());

        // deploy the topology so we build the root tree using the PaaSProvider abstract class.
        Map<String, PaaSNodeTemplate> nodeTemplates = topologyTreeBuilderService.buildPaaSNodeTemplate(topology);
        List<PaaSNodeTemplate> roots = topologyTreeBuilderService.getHostedOnTree(nodeTemplates);

        // now build the plans and check results
        StartEvent startEvent = PaaSPlanGenerator.buildPlan(roots, true);
        printPlan(startEvent);

        // TODO validation of the plan...
    }

    private void printPlan(StartEvent startStep) {
        System.out.println(startStep.getClass().getName());
        printPlan(startStep.getNextStep(), 0, true);
    }

    private void printPlan(WorkflowStep step, int level, boolean sequenceFirst) {
        if (step != null) {
            for (int i = 0; i < level; i++) {
                System.out.print("  ");
            }

            if (step instanceof StateUpdateEvent) {
                System.out.println(step.getClass().getName() + " " + ((StateUpdateEvent) step).getElementId() + "." + ((StateUpdateEvent) step).getState());
            } else if (step instanceof OperationCallActivity) {
                System.out.println(step.getClass().getName() + " " + ((OperationCallActivity) step).getInterfaceName() + "."
                        + ((OperationCallActivity) step).getOperationName());
            } else if (step instanceof ParallelJoinStateGateway) {
                System.out.println(step.getClass().getName() + " " + ((ParallelJoinStateGateway) step).getValidStatesPerElementMap());
            }

            if (step instanceof ParallelGateway) {
                List<WorkflowStep> parallelSteps = ((ParallelGateway) step).getParallelSteps();
                for (WorkflowStep workflowStep : parallelSteps) {
                    System.out.println("{");
                    printPlan(workflowStep, level + 1, true);
                    System.out.println("}");
                }
            } else {
                printPlan(step.getNextStep(), level, false);
            }
        }
    }
}