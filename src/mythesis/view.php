<?php
require_once('../../config.php');
$cmid = required_param('id', PARAM_INT);
$cm = get_coursemodule_from_id('mymodulename', $cmid, 0, false, MUST_EXIST);
$course = $DB->get_record('course', array('id' => $cm->course), '*', MUST_EXIST);
 
require_login($course, true, $cm);
$PAGE->set_url('/mod/mymodulename/view.php', array('id' => $cm->id));
$PAGE->set_title('My modules page title');
$PAGE->set_heading('My modules page heading');
 
// The rest of your code goes below this.
$PAGE->set_context(context_system::instance());
$PAGE->set_context(context_coursecat::instance($categoryid));
$PAGE->set_context(context_course::instance($courseid));
$PAGE->set_context(context_module::instance($moduleid));

$PAGE->set_pagelayout('standard');

$PAGE->set_heading(get_string('pluginname', 'local_myplugin'));
?>