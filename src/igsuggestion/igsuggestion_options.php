<?php
// This file is part of Moodle - http://moodle.org/
//
// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * Class to store the options for a {@link quiz_igsuggestion_report}.
 * Extended from Jean-Michel responses plugin code (2008)
 *
 * @package   quiz_igsuggestion
 * @copyright 2016 Try Ajitiono
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


defined('MOODLE_INTERNAL') || die();

require_once($CFG->dirroot . '/mod/quiz/report/attemptsreport_options.php');


/**
 * Class to store the options for a {@link quiz_igsuggestion_report}.
 *
 * @copyright 2012 The Open University
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
class quiz_igsuggestion_options extends mod_quiz_attempts_report_options {

    /** @var bool whether to show the question text columns. */
    public $showqtext = false;

    /** @var bool whether to show the students' reponse columns. */
    public $showigsuggestion = true;

    /** @var bool whether to show the scores for chosen Rs only. */
    public $showqdata = false;

    /** @var bool whether to show the scores for chosen Rs only. */
    public $showchosenrs = false;

    protected function get_url_params() {
        $params = parent::get_url_params();
        $params['qtext'] = $this->showqtext;
        $params['resp']  = $this->showigsuggestion;
        $params['qdata'] = $this->showqdata;
        $params['chosenrs'] = $this->showchosenrs;
        return $params;
    }

    public function get_initial_form_data() {
        $toform = parent::get_initial_form_data();
        $toform->qtext = $this->showqtext;
        $toform->resp  = $this->showigsuggestion;
        $toform->qdata = $this->showqdata;
        $toform->chosenrs = $this->showchosenrs;

        return $toform;
    }

    public function setup_from_form_data($fromform) {
        parent::setup_from_form_data($fromform);

//        $this->showqtext     = $fromform->qtext;
        $this->showigsuggestion = 1;
        $this->showqdata     = $fromform->qdata;
        $this->showchosenrs     = $fromform->chosenrs;
    }

    public function setup_from_params() {
        parent::setup_from_params();

        $this->showqtext     = optional_param('qtext', $this->showqtext,     PARAM_BOOL);
        $this->showigsuggestion = optional_param('resp',  $this->showigsuggestion, PARAM_BOOL);
        $this->showqdata     = optional_param('qdata', $this->showqdata,     PARAM_BOOL);
        $this->showchosenrs     = optional_param('chosenrs', $this->showchosenrs,     PARAM_BOOL);
    }

    public function setup_from_user_preferences() {
        parent::setup_from_user_preferences();

        $this->showqtext     = get_user_preferences('quiz_report_igsuggestion_qtext', $this->showqtext);
        $this->showigsuggestion = get_user_preferences('quiz_report_igsuggestion_resp',  $this->showigsuggestion);
        $this->showqdata     = get_user_preferences('quiz_report_igsuggestion_qdata', $this->showqdata);
        $this->showchosenrs     = get_user_preferences('quiz_report_igsuggestion_chosenrs', $this->showchosenrs);
    }

    public function update_user_preferences() {
        parent::update_user_preferences();

        set_user_preference('quiz_report_igsuggestion_qtext', $this->showqtext);
        set_user_preference('quiz_report_igsuggestion_resp',  $this->showigsuggestion);
        set_user_preference('quiz_report_igsuggestion_qdata', $this->showqdata);
        set_user_preference('quiz_report_igsuggestion_chosenrs', $this->showchosenrs);
    }

    public function resolve_dependencies() {
        parent::resolve_dependencies();

        $this->showigsuggestion = true;

        // We only want to show the checkbox to delete attempts
        // if the user has permissions and if the report mode is showing attempts.
        $this->checkboxcolumn = has_capability('mod/quiz:deleteattempts', context_module::instance($this->cm->id))
                && ($this->attempts != quiz_attempts_report::ENROLLED_WITHOUT);
    }
}
