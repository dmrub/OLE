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
              "z-index":"9999"
          }
        },

        { selector: '.autorotate',
          style: {
              'edge-text-rotation': 'autorotate' 
          }
        },

        { "selector": "node",
          "style": {
              'width': 'label',
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
              "background-color":"#911",
              "background-clip":"none",
              "background-fit":"contain",
              "shape": "ellipse",
              "content": "data(name)",
          },
        },
        
        { "selector": "node[nodeType=\"blankNode\"]",
          "style": {
              'width': 50,
              "background-clip":"none",
              "background-fit":"contain",
              "shape": 'triangle',
          }
        },

        { "selector": "node[nodeType=\"literalNode\"]",
          "style": {
              "background-color":"#191",
              "background-clip":"none",
              "background-fit":"contain",
              "shape": "rectangle",
              "content":"data(value)",
          }
        },
        
        { "selector": "node[nodeType=\"literalNode\"][valueType=\"xsd:anyURI\"]",
          "style": {
              "background-clip":"none",
              "background-fit":"contain",
              "shape": "rectangle",
              "content": "click!",
          }
        },
            
        { "selector": "edge",
          "style": {
              label: 'data(name)',
              'curve-style': 'bezier',
              'control-point-step-size': 40,
              'target-arrow-shape': 'triangle',
              "text-valign":"center",
              "text-halign":"center",
              "background-color":"#172",
              "opacity":"0.4",
              "line-color":"#a0b3dc",
              "width":"mapData(weight, 0, 1, 1, 8)",
              "overlay-padding":"3px",
              
              
          },
        },

        { "selector": "edge.highlighted",
          "style": {
              'line-color' : 'black',
              "width":2,
          }
        },
    ],
      
    elements : data,
      
    layout: { 
        name: 'cose-bilkent',
        // Called on `layoutready`
        ready: function () { },
        // Called on `layoutstop`
        stop: function () { },
        // Whether to fit the network view after when done
        fit: true,
        // Padding on fit
        padding: 10,
        // Whether to enable incremental mode
        randomize: true,
        // Node repulsion (non overlapping) multiplier
        nodeRepulsion: 1000000,
        // Ideal edge (non nested) length
        idealEdgeLength: 250,
        // Divisor to compute edge forces
        edgeElasticity: 0.45,
        // Nesting factor (multiplier) to compute ideal edge length for nested edges
        nestingFactor: 1.0,
        // Gravity force (constant)
        gravity: 0.0,
        // Maximum number of iterations to perform
        numIter: 50,
        // For enabling tiling
        tile: true,
        // Type of layout animation. The option set is {'during', 'end', false}
        animate: 'end',
        // Represents the amount of the vertical space to put between the zero degree members during the tiling operation(can also be a function)
        tilingPaddingVertical: 5000,
        // Represents the amount of the horizontal space to put between the zero degree members during the tiling operation(can also be a function)
        tilingPaddingHorizontal: 5000,
        // Gravity range (constant) for compounds
        gravityRangeCompound: 0.0,
        // Gravity force (constant) for compounds
        gravityCompound: 0.0,
        // Gravity range (constant)
        gravityRange: 0.0
    }

  });

      /*
cy.on('mouseover', 'node', function(e){
    var sel = e.cyTarget;
    cy.elements().difference(sel.outgoers()).not(sel).addClass('semitransp');
    sel.connectedEdges().addClass('highlight');
});
      
cy.on('mouseout', 'node', function(e){
    var sel = e.cyTarget;
    cy.elements().removeClass('semitransp');
    sel.connectedEdges().removeClass('highlight');
});
*/
/*      
  var nodeUnselected = cy.on('mouseout' , function(evt) {
      var sel = evt.cyTarget;
      sel.connectedEdges().removeClass('highlighted');
  })
  
  var nodeSelected = cy.on('mouseover' , function(evt) {
      var sel = evt.cyTarget;
      sel.connectedEdges().addClass('highlighted');
  })
*/
  var nodeUnselected = cy.on('unselect' , function(evt) {
      var sel = evt.cyTarget;
      sel.connectedEdges().removeClass('highlighted');
  })
  
  var nodeSelected = cy.on('select' , function(evt) {
      var sel = evt.cyTarget;
      sel.connectedEdges().addClass('highlighted');
  })
  
  cy.nodes().filter('[nodeType=\"uriNode\"]').forEach(
        function(n)
        {
            var g = n.data('name');
            n.qtip( { 
                content: [ { name: n.data('name'), url: n.data('uri') } ].map(function( link ){ 
                    return '<a target="_blank" href="' + baseUri + '#' + link.url + '">Zoom on</a><br /><br /><a target="_blank" href="' + link.url + '">Go to</a>'; }).join('<br />\n'),
                position: { my: 'top center', at: 'bottom center' },
                style: {
                    classes: 'qtip-bootstrap',
                    tip: { width: 16, height: 8 }
                }
            });
        }
  );
      
  cy.nodes().filter('[nodeType=\"uriNode\"]').forEach(
        function(n)
        {
            var g = n.data('name');
            n.qtip( { 
                content: [ { name: n.data('name'), url: n.data('uri') } ].map(function( link ){ 
                    return '<a target="_blank" href="' + baseUri + '#' + link.url + '">Zoom on</a><br /><br /><a target="_blank" href="' + link.url + '">Go to</a>'; }).join('<br />\n'),
                position: { my: 'top center', at: 'bottom center' },
                style: {
                    classes: 'qtip-bootstrap',
                    tip: { width: 16, height: 8 }
                }
            });
        }
  );

  cy.nodes().filter('node[nodeType=\"literalNode\"][valueType=\"xsd:anyURI\"]').forEach(
          function(n)
        {
            var g = n.data('name');
            n.qtip( { 
                content: [ { name: n.data('name'), url: n.data('value') } ].map(function( link ){ 
                    return '<a target="_blank" href="' + link.url + '">Follow me!</a>'; }).join('<br />\n'),
                position: { my: 'top center', at: 'bottom center' },
                style: {
                    classes: 'qtip-bootstrap',
                    tip: { width: 16, height: 8 }
                }
            });
        }
  );

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
