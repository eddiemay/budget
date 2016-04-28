com.digitald4.iis.module.directive('assessment', function() {
  return {
    'restrict': 'EAM',
    'replace': true,
    'controller': com.digitald4.iis.AssessmentCtrl,
    'templateUrl': 'html/assessment/assessment.html'
  };
});