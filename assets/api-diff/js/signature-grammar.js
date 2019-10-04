// Generated automatically by nearley, version 2.16.0
// http://github.com/Hardmath123/nearley
(function () {
function id(x) { return x[0]; }
var grammar = {
    Lexer: undefined,
    ParserRules: [
    {"name": "Main", "symbols": ["Class"]},
    {"name": "Main", "symbols": ["Field"]},
    {"name": "Main", "symbols": ["Method"]},
    {"name": "Main", "symbols": ["MethodParameter"]},
    {"name": "Class$subexpression$1$string$1", "symbols": [{"literal":"c"}, {"literal":"l"}, {"literal":"a"}, {"literal":"s"}, {"literal":"s"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Class$subexpression$1", "symbols": ["Class$subexpression$1$string$1"]},
    {"name": "Class$subexpression$1$string$2", "symbols": [{"literal":"m"}, {"literal":"i"}, {"literal":"s"}, {"literal":"s"}, {"literal":"i"}, {"literal":"n"}, {"literal":"g"}, {"literal":"-"}, {"literal":"c"}, {"literal":"l"}, {"literal":"a"}, {"literal":"s"}, {"literal":"s"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Class$subexpression$1", "symbols": ["Class$subexpression$1$string$2"]},
    {"name": "Class$subexpression$1$string$3", "symbols": [{"literal":"i"}, {"literal":"n"}, {"literal":"t"}, {"literal":"e"}, {"literal":"r"}, {"literal":"f"}, {"literal":"a"}, {"literal":"c"}, {"literal":"e"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Class$subexpression$1", "symbols": ["Class$subexpression$1$string$3"]},
    {"name": "Class$subexpression$1$string$4", "symbols": [{"literal":"e"}, {"literal":"n"}, {"literal":"u"}, {"literal":"m"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Class$subexpression$1", "symbols": ["Class$subexpression$1$string$4"]},
    {"name": "Class$subexpression$1$string$5", "symbols": [{"literal":"@"}, {"literal":"i"}, {"literal":"n"}, {"literal":"t"}, {"literal":"e"}, {"literal":"r"}, {"literal":"f"}, {"literal":"a"}, {"literal":"c"}, {"literal":"e"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Class$subexpression$1", "symbols": ["Class$subexpression$1$string$5"]},
    {"name": "Class", "symbols": ["Class$subexpression$1", "_", "Type"], "postprocess":
        function (d) {
        	var ret = d[2];
        	ret["elementType"] = d[0][0];
        	return ret;
        }
        },
    {"name": "Field$string$1", "symbols": [{"literal":"f"}, {"literal":"i"}, {"literal":"e"}, {"literal":"l"}, {"literal":"d"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Field$ebnf$1$subexpression$1", "symbols": ["_", {"literal":"@"}, "_", "Type"]},
    {"name": "Field$ebnf$1", "symbols": ["Field$ebnf$1$subexpression$1"], "postprocess": id},
    {"name": "Field$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Field", "symbols": ["Field$string$1", "_", "Type", {"literal":"."}, "Identifier", "Field$ebnf$1"], "postprocess":
        function(d) {
        	var ret = d[2];
        	ret["elementType"] = "field";
        	ret["fieldName"] = d[4];

        	return ret;
        }
        },
    {"name": "Method$string$1", "symbols": [{"literal":"m"}, {"literal":"e"}, {"literal":"t"}, {"literal":"h"}, {"literal":"o"}, {"literal":"d"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Method$ebnf$1$subexpression$1", "symbols": ["TypeParameters", "_"]},
    {"name": "Method$ebnf$1", "symbols": ["Method$ebnf$1$subexpression$1"], "postprocess": id},
    {"name": "Method$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$string$2", "symbols": [{"literal":":"}, {"literal":":"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Method$subexpression$1", "symbols": ["Identifier"]},
    {"name": "Method$subexpression$1$string$1", "symbols": [{"literal":"<"}, {"literal":"i"}, {"literal":"n"}, {"literal":"i"}, {"literal":"t"}, {"literal":">"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Method$subexpression$1", "symbols": ["Method$subexpression$1$string$1"]},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2", "symbols": []},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$2", "symbols": ["_"], "postprocess": id},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$2", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1", "symbols": ["Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$1", {"literal":","}, "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$2", "Type"]},
    {"name": "Method$ebnf$2$subexpression$1$ebnf$2", "symbols": ["Method$ebnf$2$subexpression$1$ebnf$2", "Method$ebnf$2$subexpression$1$ebnf$2$subexpression$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "Method$ebnf$2$subexpression$1", "symbols": ["Method$ebnf$2$subexpression$1$ebnf$1", "Type", "Method$ebnf$2$subexpression$1$ebnf$2"]},
    {"name": "Method$ebnf$2", "symbols": ["Method$ebnf$2$subexpression$1"], "postprocess": id},
    {"name": "Method$ebnf$2", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$3$subexpression$1$string$1", "symbols": [{"literal":"t"}, {"literal":"h"}, {"literal":"r"}, {"literal":"o"}, {"literal":"w"}, {"literal":"s"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1", "symbols": []},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1$ebnf$2", "symbols": ["_"], "postprocess": id},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1$ebnf$2", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1", "symbols": ["Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1$ebnf$1", {"literal":","}, "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1$ebnf$2", "Type"]},
    {"name": "Method$ebnf$3$subexpression$1$ebnf$1", "symbols": ["Method$ebnf$3$subexpression$1$ebnf$1", "Method$ebnf$3$subexpression$1$ebnf$1$subexpression$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "Method$ebnf$3$subexpression$1", "symbols": ["_", "Method$ebnf$3$subexpression$1$string$1", "_", "Type", "Method$ebnf$3$subexpression$1$ebnf$1"]},
    {"name": "Method$ebnf$3", "symbols": ["Method$ebnf$3$subexpression$1"], "postprocess": id},
    {"name": "Method$ebnf$3", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method$ebnf$4$subexpression$1", "symbols": ["_", {"literal":"@"}, "_", "Type"]},
    {"name": "Method$ebnf$4", "symbols": ["Method$ebnf$4$subexpression$1"], "postprocess": id},
    {"name": "Method$ebnf$4", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Method", "symbols": ["Method$string$1", "_", "Method$ebnf$1", "Type", "_", "Type", "Method$string$2", "Method$subexpression$1", {"literal":"("}, "Method$ebnf$2", {"literal":")"}, "Method$ebnf$3", "Method$ebnf$4"], "postprocess":
        function(d) {
        	var params = [];
        	if (d[9] !== null) {
        		params.push(d[9][1]);
        		d[9][2].forEach(function (e) {
        			params.push(e[3]);
        		});
        	}

        	var thrown = [];
        	if (d[11] !== null) {
        		thrown.push(d[11][3]);
        		d[11][4].forEach(function (t) {
        			thrown.push(t[3]);
        		});
        	}

        	return {
        		"elementType": "method",
        		"typeParameters": d[2] == null ? [] : d[2][0],
        		"returnType": d[3],
        		"declaringType": d[5],
        		"methodName": d[7][0],
        		"parameters": params,
        		"thrownTypes": thrown,
        		"reportingType": d[12] == null ? null : d[12][3],
        	};
        }
        },
    {"name": "MethodParameter$string$1", "symbols": [{"literal":"p"}, {"literal":"a"}, {"literal":"r"}, {"literal":"a"}, {"literal":"m"}, {"literal":"e"}, {"literal":"t"}, {"literal":"e"}, {"literal":"r"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "MethodParameter$ebnf$1$subexpression$1", "symbols": ["TypeParameters", "_"]},
    {"name": "MethodParameter$ebnf$1", "symbols": ["MethodParameter$ebnf$1$subexpression$1"], "postprocess": id},
    {"name": "MethodParameter$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "MethodParameter$string$2", "symbols": [{"literal":":"}, {"literal":":"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$subexpression$1", "symbols": ["Type"]},
    {"name": "MethodParameter$ebnf$2$subexpression$1$subexpression$1$string$1", "symbols": [{"literal":"="}, {"literal":"="}, {"literal":"="}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$subexpression$1$string$2", "symbols": [{"literal":"="}, {"literal":"="}, {"literal":"="}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$subexpression$1", "symbols": ["MethodParameter$ebnf$2$subexpression$1$subexpression$1$string$1", "Type", "MethodParameter$ebnf$2$subexpression$1$subexpression$1$string$2"]},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2", "symbols": []},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$2", "symbols": ["_"], "postprocess": id},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$2", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1", "symbols": ["Type"]},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1$string$1", "symbols": [{"literal":"="}, {"literal":"="}, {"literal":"="}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1$string$2", "symbols": [{"literal":"="}, {"literal":"="}, {"literal":"="}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1", "symbols": ["MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1$string$1", "Type", "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1$string$2"]},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1", "symbols": ["MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$1", {"literal":","}, "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$ebnf$2", "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1$subexpression$1"]},
    {"name": "MethodParameter$ebnf$2$subexpression$1$ebnf$2", "symbols": ["MethodParameter$ebnf$2$subexpression$1$ebnf$2", "MethodParameter$ebnf$2$subexpression$1$ebnf$2$subexpression$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "MethodParameter$ebnf$2$subexpression$1", "symbols": ["MethodParameter$ebnf$2$subexpression$1$ebnf$1", "MethodParameter$ebnf$2$subexpression$1$subexpression$1", "MethodParameter$ebnf$2$subexpression$1$ebnf$2"]},
    {"name": "MethodParameter$ebnf$2", "symbols": ["MethodParameter$ebnf$2$subexpression$1"], "postprocess": id},
    {"name": "MethodParameter$ebnf$2", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "MethodParameter$ebnf$3$subexpression$1", "symbols": ["_", {"literal":"@"}, "_", "Type"]},
    {"name": "MethodParameter$ebnf$3", "symbols": ["MethodParameter$ebnf$3$subexpression$1"], "postprocess": id},
    {"name": "MethodParameter$ebnf$3", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "MethodParameter", "symbols": ["MethodParameter$string$1", "_", "MethodParameter$ebnf$1", "Type", "_", "Type", "MethodParameter$string$2", "Identifier", {"literal":"("}, "MethodParameter$ebnf$2", {"literal":")"}, "MethodParameter$ebnf$3"], "postprocess":

        function(d) {
        	var params = [];
        	if (d[9][1].length === 1) {
        		params.push(d[9][1][0]);
        	} else {
        		params.push(d[9][1][1]);
        	}

        	var selected = 0;

        	var idx = 0;
        	d[9][2].forEach(function (e) {
        		var ar = e[3];
        		if (ar.length === 1) {
        			params.push(e[3][0]);
        		} else {
        			params.push(e[3][1]);
        			selected = idx + 1;
        		}
        		idx++;
        	});

        	return {
        		"elementType": "parameter",
        		"typeParameters": d[2] == null ? [] : d[2][0],
        		"returnType": d[3],
        		"declaringType": d[5],
        		"methodName": d[7],
        		"parameters": params,
        		"selectedParameterIndex": selected,
        		"reportingType": d[11] == null ? null : d[11][3],
        		"__": d
        	};
        }
        },
    {"name": "Type$ebnf$1", "symbols": ["TypeParameters"], "postprocess": id},
    {"name": "Type$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "Type$ebnf$2", "symbols": []},
    {"name": "Type$ebnf$2$string$1", "symbols": [{"literal":"["}, {"literal":"]"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "Type$ebnf$2", "symbols": ["Type$ebnf$2", "Type$ebnf$2$string$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "Type", "symbols": ["ClassName", "Type$ebnf$1", "Type$ebnf$2"], "postprocess":
        function(d) {
        	return {
        		"type": d[0],
        		"typeParameters": d[1],
        		"arrayDimension":  d[2] == null ? 0 : d[2].length,
        	};
        }
        },
    {"name": "ClassName$ebnf$1", "symbols": []},
    {"name": "ClassName$ebnf$1$subexpression$1", "symbols": [{"literal":"."}, "Identifier"]},
    {"name": "ClassName$ebnf$1", "symbols": ["ClassName$ebnf$1", "ClassName$ebnf$1$subexpression$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "ClassName", "symbols": ["Identifier", "ClassName$ebnf$1"], "postprocess":
        function(d) { return d[0] + d[1].map(function(e) {return e.join("")}).join(""); }
        },
    {"name": "Identifier$ebnf$1", "symbols": []},
    {"name": "Identifier$ebnf$1", "symbols": ["Identifier$ebnf$1", /[a-zA-Z0-9$_>]/], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "Identifier", "symbols": [/[<a-zA-Z$_]/, "Identifier$ebnf$1"], "postprocess":
        function(d) { return d[0] + d[1].join(""); }
        },
    {"name": "TypeParameters$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "TypeParameters$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "TypeParameters$ebnf$2", "symbols": []},
    {"name": "TypeParameters$ebnf$2$subexpression$1$ebnf$1", "symbols": ["_"], "postprocess": id},
    {"name": "TypeParameters$ebnf$2$subexpression$1$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "TypeParameters$ebnf$2$subexpression$1$ebnf$2", "symbols": ["_"], "postprocess": id},
    {"name": "TypeParameters$ebnf$2$subexpression$1$ebnf$2", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "TypeParameters$ebnf$2$subexpression$1", "symbols": ["TypeParameters$ebnf$2$subexpression$1$ebnf$1", {"literal":","}, "TypeParameters$ebnf$2$subexpression$1$ebnf$2", "TypeParameter"]},
    {"name": "TypeParameters$ebnf$2", "symbols": ["TypeParameters$ebnf$2", "TypeParameters$ebnf$2$subexpression$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "TypeParameters$ebnf$3", "symbols": ["_"], "postprocess": id},
    {"name": "TypeParameters$ebnf$3", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "TypeParameters", "symbols": [{"literal":"<"}, "TypeParameters$ebnf$1", "TypeParameter", "TypeParameters$ebnf$2", "TypeParameters$ebnf$3", {"literal":">"}], "postprocess":
        function (d) {
        	var ret = [d[2]];

         		d[3].forEach(function (e) {
         			ret.push(e[3]);
         		});

         		return ret;
        }
        },
    {"name": "TypeParameter$subexpression$1", "symbols": [{"literal":"?"}]},
    {"name": "TypeParameter$subexpression$1", "symbols": ["Type"]},
    {"name": "TypeParameter$ebnf$1$subexpression$1", "symbols": ["_", "TypeParameterBound"]},
    {"name": "TypeParameter$ebnf$1", "symbols": ["TypeParameter$ebnf$1$subexpression$1"], "postprocess": id},
    {"name": "TypeParameter$ebnf$1", "symbols": [], "postprocess": function(d) {return null;}},
    {"name": "TypeParameter", "symbols": ["TypeParameter$subexpression$1", "TypeParameter$ebnf$1"], "postprocess":
        function (d) {
        	return {
        		"type": d[0][0],
        		"bound": d[1] == null ? null : d[1][1],
        	};
        }
        },
    {"name": "TypeParameterBound$subexpression$1$string$1", "symbols": [{"literal":"e"}, {"literal":"x"}, {"literal":"t"}, {"literal":"e"}, {"literal":"n"}, {"literal":"d"}, {"literal":"s"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "TypeParameterBound$subexpression$1", "symbols": ["TypeParameterBound$subexpression$1$string$1"]},
    {"name": "TypeParameterBound$subexpression$1$string$2", "symbols": [{"literal":"s"}, {"literal":"u"}, {"literal":"p"}, {"literal":"e"}, {"literal":"r"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "TypeParameterBound$subexpression$1", "symbols": ["TypeParameterBound$subexpression$1$string$2"]},
    {"name": "TypeParameterBound$ebnf$1", "symbols": []},
    {"name": "TypeParameterBound$ebnf$1$subexpression$1", "symbols": ["_", "TypeParameterBound"]},
    {"name": "TypeParameterBound$ebnf$1", "symbols": ["TypeParameterBound$ebnf$1", "TypeParameterBound$ebnf$1$subexpression$1"], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "TypeParameterBound", "symbols": ["TypeParameterBound$subexpression$1", "_", "Type", "TypeParameterBound$ebnf$1"], "postprocess":
        function (d) {
        	return {
        		"kind": d[0][0],
        		"boundType": d[2],
        		"specialization": d[3].length > 0 ? d[3][0][1] : null,
        	};
        }
        },
    {"name": "_$ebnf$1", "symbols": [/[\s]/]},
    {"name": "_$ebnf$1", "symbols": ["_$ebnf$1", /[\s]/], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "_", "symbols": ["_$ebnf$1"], "postprocess": function(d) {return null }}
]
  , ParserStart: "Main"
}
if (typeof module !== 'undefined'&& typeof module.exports !== 'undefined') {
   module.exports = grammar;
} else {
   window.grammar = grammar;
}
})();
