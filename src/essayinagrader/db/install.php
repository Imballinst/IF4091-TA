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
 * Post-install script for the qtype essayinagrader.
 *
 * @package   qtype_essayinagrader
 * @copyright 2016 Try Ajitiono
 * @license   http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */


defined('MOODLE_INTERNAL') || die();


/**
 * Post-install script
 */
function xmldb_qtype_essayinagrader_install() {
    global $DB;

    $sql1 = file_get_contents(dirname(getcwd()) . "\\question\\type\\essayinagrader\\db\\synonyms.txt");

    $arr = explode("\n", $sql1);
    $stack = array();
    $int = 0;
    foreach ($arr as $ar) {
      $str = explode(" ", $ar, 3);
      $obj = new stdClass();
      $obj->wordid = $str[0];
      $obj->pos = $str[1];
      $obj->word = $str[2];
      $stack[] = $obj;
      // $int++;
      // if ($int == 35999) {
      //   $DB->insert_records('qtype_essayinagrader_syns', $stack);
      //   $int = 0;
      //   $stack = array();
      // }
    }
    $DB->insert_records('qtype_essayinagrader_syns', $stack);
}
