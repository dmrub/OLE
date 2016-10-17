$(function(){ // on dom ready
    if (window.location.hash.substr(1).length>0)
    var uri = window.location.hash.substr(1);
    else 
    var uri = "http://ole-frontend/repo";
    
  function schwachsinn(data){
        
      var baseUri = window.location.href.split('#')[0];


      
  var cy = cytoscape({
    container: document.getElementById('cy'),
    
    style: [
        { "selector": "core",
          "style": {
              "selection-box-color":"#AAD8FF",
              "selection-box-border-color":"#8BB0D0",
              "selection-box-opacity":"0.5"
          }
        },

        { "selector":".highlighted",
          "style": {
              "z-index":"999999"
          }
        },

        { selector: '.autorotate',
          style: {
              'edge-text-rotation': 'autorotate' 
          }
        },

        { "selector": "node",
          "style": {
              "font-size":"12px",
              "text-valign":"center",
              "text-halign":"center",
              "background-color":"#555",
              "text-outline-color":"#555",
              "text-outline-width":"2px",
              "color":"#fff",
              "overlay-padding":"6px",
              "z-index":"10"
          }
        },
        
        { "selector": "node:selected",
          "style": {
              "border-width":"6px",
              "border-color":"#AAD8FF",
              "border-opacity":"0.5",
              "background-color":"#77828C",
              "text-outline-color":"#77828C"
          }
        },

        { "selector":"node.highlighted",
          "style": {
              "border-width":"6px",
              "border-color":"#AAD8FF",
              "border-opacity":"0.5",
              "background-color":"#394855",
              "text-outline-color":"#394855",
              "shadow-blur":"12px",
              "shadow-color":"#000",
              "shadow-opacity":"0.8",
              "shadow-offset-x":"0px",
              "shadow-offset-y":"4px"
          }
        },

        { "selector": "node.unhighlighted",
          "style": {
              "opacity":"0.2"
          }
        },
 
        { "selector": "node[nodeType=\"uriNode\"]",
          "style": {
              "width":"label"*2,
              "height":"label"*2,
              "background-clip":"none",
              "background-fit":"contain",
              "shape": "ellipse",
              "content": "data(name)",
          },
        },
        
        { "selector": "node[nodeType=\"blankNode\"]",
          "style": {
              "background-clip":"none",
              "background-fit":"contain",
              "shape": 'triangle',
          }
        },

        { "selector": "node[nodeType=\"literalNode\"]",
          "style": {
              "width":"label",
              "height":"label",
              "background-clip":"none",
              "background-fit":"contain",
              "shape": "rectangle",
              "content":"data(value)",
          }
        },
            
        { "selector": "edge",
          "style": {
              "curve-style":"haystack",
              'target-arrow-shape': 'triangle',
              "haystack-radius":"0.5",
              "text-valign":"center",
              "text-halign":"center",
              "background-color":"#172",
              "opacity":"0.4",
              "line-color":"#a0b3dc",
              "width":"mapData(weight, 0, 1, 1, 8)",
              "overlay-padding":"3px",
              label: 'data(name)'
              
          },
        },
        
        { "selector": "edge.unhighlighted",
          "style": {
              "opacity":"0.05"
          }
        },
        
        { "selector": "edge.filtered",
          "style": {
              "opacity":"0"
          }
        },
    ],
      
    elements : data,
  })
  
  var params = {
    name: 'cola',
    nodeSpacing: 5,
    edgeLengthVal: 45,
    animate: true,
    randomize: false,
    maxSimulationTime: 1500


  };

  var layout = makeLayout();
  var running = false;

  cy.on('layoutstart', function(){
    running = true;
  }).on('layoutstop', function(){
    running = false;
  });
  
  layout.run();

  var $config = $('#config');
  var $btnParam = $('<div class="param"></div>');
  $config.append( $btnParam );

  var sliders = [
    {
      label: 'Edge length',
      param: 'edgeLengthVal',
      min: 1,
      max: 200
    },

    {
      label: 'Node spacing',
      param: 'nodeSpacing',
      min: 1,
      max: 50
    }
  ];

  var buttons = [
    {
      label: '<i class="fa fa-random"></i>',
        fn: function(){ cy.edges().filter('[name = \"ADMS:includedAsset\"]').targets().hide(); cy.edges().filter('[name = \"ADMS:includedAsset\"]').hide();},
      layoutOpts: {
        randomize: true,
        flow: null
      }
    },

    {
      label: '<i class="fa fa-long-arrow-down"></i>',
        fn: function(){ cy.edges().filter('[name = \"ADMS:includedAsset\"]').targets().hide(); cy.edges().filter('[name = \"ADMS:includedAsset\"]').hide();},
      layoutOpts: {
        flow: { axis: 'y', minSeparation: 40 }
      }
    },
      
    {
      label: '<i class="fa fa-filter"></i>',
      fn: function(){ cy.edges().filter('[name = \"ADMS:includedAsset\"]').targets().hide(); cy.edges().filter('[name = \"ADMS:includedAsset\"]').hide();},
      layoutOpts: {
        randomize: true,
        flow: null
      }
    }
    
  ];

  sliders.forEach( makeSlider );

  buttons.forEach( makeButton );

  function makeLayout( opts ){
    params.randomize = false;
    params.edgeLength = function(e){ return params.edgeLengthVal / e.data('weight'); };
      

    for( var i in opts ){
      params[i] = opts[i];
    }

    return cy.makeLayout( params );
  }

  function makeSlider( opts ){
    var $input = $('<input></input>');
    var $param = $('<div class="param"></div>');

    $param.append('<span class="label label-default">'+ opts.label +'</span>');
    $param.append( $input );

    $config.append( $param );

    var p = $input.slider({
      min: opts.min,
      max: opts.max,
      value: params[ opts.param ]
    }).on('slide', _.throttle( function(){
      params[ opts.param ] = p.getValue();

      layout.stop();
      layout = makeLayout();
      layout.run();
    }, 16 ) ).data('slider');
  }

  function makeButton( opts ){
    var $button = $('<button class="btn btn-default">'+ opts.label +'</button>');
    
    $btnParam.append( $button );

    $button.on('click', function(){
      layout.stop();

      if( opts.fn ){ opts.fn(); }

      layout = makeLayout( opts.layoutOpts );
      layout.run();
    });
  }
       
  cy.nodes().filter('[nodeType=\"uriNode\"]').forEach(
        function(n)
        {
            var g = n.data('name');
            n.qtip( { 
                content: [ { name: n.data('name'), url: n.data('uri') } ].map(function( link ){ return '<a target="_blank" href="' + baseUri + '#' + link.url + '">Zoom on</a><br /><br /><a target="_blank" href="' + link.url + '">Go to</a>'; }).join('<br />\n'),
                position: { my: 'top center', at: 'bottom center' },
                style: {
                    classes: 'qtip-bootstrap',
                    tip: { width: 16, height: 8 }
                }
            });
        }
  );
    
  $('#config-toggle').on('click', function(){
    $('body').toggleClass('config-closed');

    cy.resize();
  });
};

    
  $.ajax( {
    url:      "http://cytoscape-frontend/api/visualize?uri=" + uri,
    dataType: 'json',
    async: true,
    success: function(data) { schwachsinn(data) } });




}); // on dom ready

$(function() {
  FastClick.attach( document.body );
});
