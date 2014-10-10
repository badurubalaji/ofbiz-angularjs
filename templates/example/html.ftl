
<div ng-controller="org.ofbiz.angularjs.demo.controller.FormDemoController">
<form name="signup_form" novalidate ng-submit="signupForm()">
  <fieldset>
    <legend>Signup</legend>
    <div class="row">
      <div class="large-12 columns">
        <label>Your name</label>
        <input type="text"
            placeholder="Name"
            name="name"
            ng-model="signup.name"
            ng-minlength=3
            ng-maxlength=20 required />
       <div class="error-container"
            ng-show="signup_form.name.$dirty && signup_form.name.$invalid && signup_form.submitted">
        <small class="error"
            ng-show="signup_form.name.$error.required">
            Your name is required.
        </small>
        <small class="error"
                ng-show="signup_form.name.$error.minlength">
                Your name is required to be at least 3 characters
        </small>
        <small class="error"
                ng-show="signup_form.name.$error.maxlength">
                Your name cannot be longer than 20 characters
        </small>
      </div>
    </div>
    <button type="submit" class="button radius">Submit</button>
  </fieldset>
</form>
</div>



