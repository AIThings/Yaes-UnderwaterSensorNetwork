# Yaes-UnderwaterSensorNetwork

Underwater sensor networks (UWSNs) face specific challenges due to the transmission properties in the underwater environment.
Radio waves propagate only for short distances under water, and acoustic transmissions have limited data rate and relatively high latency. One of the possible solutions to these challenges involves the use of autonomous underwater vehicles (AUVs) to visit and offload data from the individual sensor nodes. We consider an underwater sensor network visually monitoring
an offshore oil platform for hazards such as oil spills from pipes and blowups. To each observation chunk (image or video)
we attach a numerical value of information (VoI). This value monotonically decreases in time with a speeed which depends
on the urgency of the captured data. An AUV visits different nodes along a specific path and collects data to be transmitted to the customer. Our objective is to develop path planners for the movement of the AUV which maximizes the total VoI collected.
