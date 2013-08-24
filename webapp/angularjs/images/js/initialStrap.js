/***********************************************
APACHE OPEN FOR BUSINESS
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
***********************************************/

// Docs styles
//
$(function() {
  $("html").removeClass("no-js").addClass("js");
    var $window = $(window);
  new FastClick(document.body);
    $(document).ready(function($) {
        // Disable certain links in docs
        $('section [href^=#]').click(function (e) {
            e.preventDefault();
        });
        // Make code pretty
        window.prettyPrint && window.prettyPrint();
    });
});
