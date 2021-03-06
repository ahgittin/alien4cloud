package alien4cloud.topology.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import alien4cloud.paas.ha.AllocationErrorCode;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("PMD.UnusedPrivateField")
public class HAGroupTask extends TopologyTask {

    private AllocationErrorCode errorCode;

    private String groupId;

    public HAGroupTask(String nodeTemplateName, String groupId, AllocationErrorCode errorCode) {
        super(TaskCode.HA_INVALID, nodeTemplateName, null);
        this.groupId = groupId;
        this.errorCode = errorCode;
    }
}
