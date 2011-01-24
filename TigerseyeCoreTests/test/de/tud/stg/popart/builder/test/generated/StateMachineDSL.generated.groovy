package de.tud.stg.popart.builder.test.generated;

import de.tud.stg.popart.builder.utils.DSLInvoker;

import de.tud.stg.dslbench.popart.fsm.* ;
import de.tud.stg.popart.builder.test.statemachine.StateMachineDSL;
import de.tud.stg.popart.builder.test.statemachine.ActionType;
	 
new DSLInvoker ( ModifiedStateMachineDSL.class ) .eval ( ) {
	
	boolean variabilityFlag = true;
	
	StateMachine m = machine__p0_lcub_p1_rcub(
Watch,
[
start__state__p0_lcub_p1__p2_rcub(
reseted,
[
entry_colon_p0_semi(
output__p0(
"entering reseted")),
exit_colon_p0_semi(
output__p0(
"leaving reseted"))
] as ActionType[],
transitions_lcub_p0_rcub(
{
p0_rarr_p1_semi(
start,
running)
				 	 
				     if (variabilityFlag) { 
  					   p0_rarr_p1_semi(
switchOff,
off)
  					 } else {
  					   p0_rarr_p1_semi(
switchOff,
paused)  					 
  					 }}
)),
state__p0_lcub_p1__p2_rcub(
running,
[
entry_colon_p0_semi(
output__p0(
"entering running")),
exit_colon_p0_semi(
output__p0(
"leaving running"))
] as ActionType[],
transitions_lcub_p0_rcub(
{
p0_rarr_p1_semi(
split,
paused)
					 p0_rarr_p1_semi(
stop,
stopped)}
)),
state__p0_lcub_p1__p2_rcub(
paused,
[
entry_colon_p0_semi(
output__p0(
"entering paused")),
exit_colon_p0_semi(
output__p0(
"leaving paused"))
] as ActionType[],
transitions_lcub_p0_rcub(
{
p0_rarr_p1_semi(
unsplit,
running)
					 p0_rarr_p1_semi(
stop,
Stopped)}
)),
state__p0_lcub_p1__p2_rcub(
stopped,
[
entry_colon_p0_semi(
output__p0(
"entering stopped")),
exit_colon_p0_semi(
output__p0(
"leaving stopped"))
] as ActionType[],
transitions_lcub_p0_rcub(
{
p0_rarr_p1_semi(
reset,
stopped)
					 p0_rarr_p1_semi(
switchOff,
off)}
)),
state__p0_lcub_p1__p2_rcub(
off,
[
entry_colon_p0_semi(
output__p0(
"entering off")),
exit_colon_p0_semi(
output__p0(
"leaving off"))
] as ActionType[],
transitions_lcub_p0_rcub(
{
p0_rarr_p1_semi(
toEnd,
reseted)}
))
] as State[])
			
	m.start();
	m.input("start");
	m.input("split");
	m.input("unsplit");
	m.input("stop");
	m.input("switchOff");
	m.input("toEnd");
}