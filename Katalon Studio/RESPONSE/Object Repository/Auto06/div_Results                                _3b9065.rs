<?xml version="1.0" encoding="UTF-8"?>
<WebElementEntity>
   <description></description>
   <name>div_Results                                _3b9065</name>
   <tag></tag>
   <elementGuidId>05f352b1-2f9f-4a58-ba35-dc1735a3d40a</elementGuidId>
   <selectorCollection>
      <entry>
         <key>CSS</key>
         <value>div.lf-results-section.lf-graphs.lf-long-answer</value>
      </entry>
      <entry>
         <key>XPATH</key>
         <value>//div[@id='lf_main']/div/div[3]/div[3]/div</value>
      </entry>
   </selectorCollection>
   <selectorMethod>XPATH</selectorMethod>
   <smartLocatorCollection>
      <entry>
         <key>SMART_LOCATOR</key>
         <value>internal:text=&quot;Results Loan 2 will cost you, in today's dollars, $18,161 less than Loan 1 Loan &quot;i</value>
      </entry>
   </smartLocatorCollection>
   <smartLocatorEnabled>false</smartLocatorEnabled>
   <useRalativeImagePath>true</useRalativeImagePath>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>tag</name>
      <type>Main</type>
      <value>div</value>
      <webElementGuid>ff2660b2-2fdf-4f8e-a08b-eef44e3a4349</webElementGuid>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>class</name>
      <type>Main</type>
      <value>lf-results-section lf-graphs  lf-long-answer</value>
      <webElementGuid>dedbe404-2ec3-4a43-a049-2594f16f6394</webElementGuid>
   </webElementProperties>
   <webElementProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>text</name>
      <type>Main</type>
      <value>
                    Results
                    
                        Loan 2 will cost you, in today's dollars, $18,161 less than Loan 1
                    
                    
                        Loan 2 will cost you, in today's dollars, $18,161 less than Loan 1

                        Show details

                        

                            
                                Results details
                                    
                                    
                                        
                                        Loan 1
                                        Loan 2
                                    
                                    
                                    

                                    
                                        
                                            Monthly payment
                                        
                                        $875
                                        
                                        $570
                                        
                                    
                                    
                                        
                                            Total interest paid
                                        
                                        $4,005
                                        
                                        $688
                                        
                                    
                                    
                                
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;div class=&quot;lf-results-list lf-list-{{listNumber}}&quot;>
                                    {{#entries}}
                                    &lt;div id=&quot;lf_{{dataElementName}}_results&quot; class=&quot;lf-result-list-content&quot;>
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;lf-details-list-label&quot;>
                                            {{{fieldLabel}}}
                                        &lt;/div>
                                        {{#formattedValues}}
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_value&quot;
                                             class=&quot;lf-details-list-value&quot;>{{value}}
                                        &lt;/div>
                                        {{/formattedValues}}
                                    &lt;/div>
                                    {{/entries}}
                                &lt;/div>
                                {{/.}}
                                {{/text}}
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;tableCellADA&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;table class=&quot;lf-results-table&quot; id=&quot;lf-results-table-main&quot;>
                                    &lt;thead>
                                    &lt;tr>
                                        {{#columnHeaders}}
                                        &lt;td>&lt;/td>
                                        {{#columnHeader}}
                                        &lt;th class=&quot;lf-results-header tableCellADA&quot; scope=&quot;col&quot;>{{.}}&lt;/th>
                                        {{/columnHeader}}
                                        {{/columnHeaders}}
                                    &lt;/tr>
                                    &lt;/thead>
                                    &lt;tbody>
                                    {{#subtitle}}
                                    &lt;tr>
                                        &lt;th id=&quot;lf_result_subtitle1&quot;
                                            class=&quot;lf-result-subtitle tableCellADA three-headers&quot; colspan=&quot;100%&quot;>
                                            {{{subtitle}}}
                                        &lt;/th>
                                    &lt;/tr>
                                    {{/subtitle}}

                                    {{#entries}}
                                    &lt;tr id=&quot;lf_{{dataElementName}}_results&quot;>
                                        &lt;th id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;tableCellADA&quot; scope=&quot;row&quot;>
                                            {{#negLabel}}&lt;span class=&quot;lf-minus-sign&quot;>&amp;minus;&lt;/span>{{/negLabel}}{{{fieldLabel}}}
                                            {{#glossaryTerm}}
                                            &lt;a role=&quot;button&quot; href=&quot;#{{glossaryTerm}}&quot;
                                               id=&quot;lf_{{dataElementName}}_tooltip&quot;
                                               class=&quot;lf-show-tooltip lf_{{dataElementName}}_tooltip lf-more-details-tooltip&quot;
                                               {{#tooltipAriaLabel}}aria-label=&quot;{{{tooltipAriaLabel}}}&quot;{{/tooltipAriaLabel}}>
                                            &lt;div aria-hidden=&quot;true&quot; class=&quot;lf-tooltip-i lf-results-tooltip&quot;>i&lt;/div>
                                            &lt;/a>
                                            {{/glossaryTerm}}
                                        &lt;/th>
                                        {{#formattedValues}}
                                        &lt;td id=&quot;lf_{{dataElementName}}_result_value&quot;
                                            class=&quot;tableCellADA {{shownClasses}}&quot;>&lt;span class=&quot;lf-nbsp-number&quot;>{{#negSymbol}}&lt;span class=&quot;lf-minus-sign {{negSymbolClass}}&quot;>&amp;minus;&lt;/span>{{/negSymbol}}{{value}}&lt;/span>
                                        &lt;/td>
                                        {{/formattedValues}}
                                    &lt;/tr>
                                    {{/entries}}
                                    &lt;/tbody>
                                &lt;/table>
                                {{/.}}
                                {{/text}}
                            
                        

                    
                    Next Steps

                    
                        Enable color patterns
                            
                                Total cost, in today's dollars, of Loan 1 and Loan 2Bar chart with 2 data series.View as data table, Total cost, in today's dollars, of Loan 1 and Loan 2The chart has 1 X axis displaying categories. The chart has 1 Y axis displaying values. Data ranges from 5824 to 23985.Chart context menuTotal cost, in today's dollars, of Loan 1 and Loan 2Loan 1Loan 2020k5k10k15k25k30kTotal CostLoan 2: $5,824End of interactive chart.
                            
                            
                                
                            
                            
                                
                                    
                                        Payment Schedule - Loan 1
                                        
                                            SEE TABLE
                                        
                                    
                                    
                                        Payment Schedule - Loan 2
                                        
                                            SEE TABLE
                                        
                                    
                                
                            
                        
                        Change views between Total Cost graph, Payments graph, and Amortization tables
                        
                            
                            
                                
                                Total Cost
                            
                            
                                
                                Payments
                            
                            
                                
                                Amortization
                            
                        
                    
                    
                        &lt;div class=&quot;lf-graph-list&quot;>
                            {{#.}}
                            {{^table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph{{#first}} lf-active{{/first}}&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div id=&quot;{{id}}&quot; class=&quot;lf-cm-90 lf-graph_container&quot;>&lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{#table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph lf-hidden&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div class=&quot;lf-tables-links&quot;>
                                    {{#tables}}
                                    &lt;div>
                                        {{#label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-multiple&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-multiple&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                        {{^label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-single&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-single&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                    &lt;/div>
                                    {{/tables}}
                                &lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{/.}}
                        &lt;/div>
                        &lt;div id=&quot;lf_selectors_description&quot; class=&quot;visually-hidden&quot;>&lt;/div>
                        &lt;div class=&quot;lf-results-loop&quot; role=&quot;tablist&quot; aria-labelledby=&quot;lf_selectors_description&quot;>
                            &lt;span class=&quot;lf-divider&quot;>&lt;/span>
                            {{#.}}
                            &lt;button id=&quot;lf_graph_tabs_{{id}}&quot; class=&quot;{{#first}}lf-active {{/first}}{{#last}}lf-last-loop{{/last}} lf-graph-tabs&quot; role=&quot;tab&quot; aria-selected=&quot;false&quot; aria-controls=&quot;{{id}}-graph-container&quot; tabindex=&quot;-1&quot;>
                                &lt;span class=&quot;lf-dot&quot; aria-hidden=&quot;true&quot;>&lt;/span>
                                &lt;div class=&quot;lf-dot-holder lf-dot-label&quot;>{{label}}&lt;/div>
                            &lt;/button>
                            {{/.}}
                        &lt;/div>
                    
                    
                        
                    
                </value>
      <webElementGuid>0be0996a-f341-4196-bda1-17ea10ec50d8</webElementGuid>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath</name>
      <type>Main</type>
      <value>id(&quot;lf_main&quot;)/div[@class=&quot;lf-meter lf-main-inner&quot;]/div[@class=&quot;lf-meter lf-meter-content&quot;]/div[@class=&quot;lf-cm-65 lf-left-col&quot;]/div[@class=&quot;lf-results-section lf-graphs  lf-long-answer&quot;]</value>
      <webElementGuid>6ed90769-6737-4734-a5ef-f82e84d79f30</webElementGuid>
   </webElementProperties>
   <webElementXpaths>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:idRelative</name>
      <type>Main</type>
      <value>//div[@id='lf_main']/div/div[3]/div[3]/div</value>
      <webElementGuid>1cdf743f-a58d-416e-88b2-fd869e88a52c</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:neighbor</name>
      <type>Main</type>
      <value>(.//*[normalize-space(text()) and normalize-space(.)='In this tool, the results display before the inputs.'])[1]/following::div[5]</value>
      <webElementGuid>199d71d1-29ee-4bb5-a4d4-68069971742b</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:neighbor</name>
      <type>Main</type>
      <value>(.//*[normalize-space(text()) and normalize-space(.)='Help'])[1]/following::div[6]</value>
      <webElementGuid>f3121800-6d95-47a1-986d-0a07bcfc0682</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:position</name>
      <type>Main</type>
      <value>//div[3]/div[3]/div</value>
      <webElementGuid>87474e05-ae83-4976-bd9d-85424db9e3b0</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:customAttributes</name>
      <type>Main</type>
      <value>//div[(text() = concat(&quot;
                    Results
                    
                        Loan 2 will cost you, in today&quot; , &quot;'&quot; , &quot;s dollars, $18,161 less than Loan 1
                    
                    
                        Loan 2 will cost you, in today&quot; , &quot;'&quot; , &quot;s dollars, $18,161 less than Loan 1

                        Show details

                        

                            
                                Results details
                                    
                                    
                                        
                                        Loan 1
                                        Loan 2
                                    
                                    
                                    

                                    
                                        
                                            Monthly payment
                                        
                                        $875
                                        
                                        $570
                                        
                                    
                                    
                                        
                                            Total interest paid
                                        
                                        $4,005
                                        
                                        $688
                                        
                                    
                                    
                                
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;div class=&quot;lf-results-list lf-list-{{listNumber}}&quot;>
                                    {{#entries}}
                                    &lt;div id=&quot;lf_{{dataElementName}}_results&quot; class=&quot;lf-result-list-content&quot;>
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;lf-details-list-label&quot;>
                                            {{{fieldLabel}}}
                                        &lt;/div>
                                        {{#formattedValues}}
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_value&quot;
                                             class=&quot;lf-details-list-value&quot;>{{value}}
                                        &lt;/div>
                                        {{/formattedValues}}
                                    &lt;/div>
                                    {{/entries}}
                                &lt;/div>
                                {{/.}}
                                {{/text}}
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;tableCellADA&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;table class=&quot;lf-results-table&quot; id=&quot;lf-results-table-main&quot;>
                                    &lt;thead>
                                    &lt;tr>
                                        {{#columnHeaders}}
                                        &lt;td>&lt;/td>
                                        {{#columnHeader}}
                                        &lt;th class=&quot;lf-results-header tableCellADA&quot; scope=&quot;col&quot;>{{.}}&lt;/th>
                                        {{/columnHeader}}
                                        {{/columnHeaders}}
                                    &lt;/tr>
                                    &lt;/thead>
                                    &lt;tbody>
                                    {{#subtitle}}
                                    &lt;tr>
                                        &lt;th id=&quot;lf_result_subtitle1&quot;
                                            class=&quot;lf-result-subtitle tableCellADA three-headers&quot; colspan=&quot;100%&quot;>
                                            {{{subtitle}}}
                                        &lt;/th>
                                    &lt;/tr>
                                    {{/subtitle}}

                                    {{#entries}}
                                    &lt;tr id=&quot;lf_{{dataElementName}}_results&quot;>
                                        &lt;th id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;tableCellADA&quot; scope=&quot;row&quot;>
                                            {{#negLabel}}&lt;span class=&quot;lf-minus-sign&quot;>&amp;minus;&lt;/span>{{/negLabel}}{{{fieldLabel}}}
                                            {{#glossaryTerm}}
                                            &lt;a role=&quot;button&quot; href=&quot;#{{glossaryTerm}}&quot;
                                               id=&quot;lf_{{dataElementName}}_tooltip&quot;
                                               class=&quot;lf-show-tooltip lf_{{dataElementName}}_tooltip lf-more-details-tooltip&quot;
                                               {{#tooltipAriaLabel}}aria-label=&quot;{{{tooltipAriaLabel}}}&quot;{{/tooltipAriaLabel}}>
                                            &lt;div aria-hidden=&quot;true&quot; class=&quot;lf-tooltip-i lf-results-tooltip&quot;>i&lt;/div>
                                            &lt;/a>
                                            {{/glossaryTerm}}
                                        &lt;/th>
                                        {{#formattedValues}}
                                        &lt;td id=&quot;lf_{{dataElementName}}_result_value&quot;
                                            class=&quot;tableCellADA {{shownClasses}}&quot;>&lt;span class=&quot;lf-nbsp-number&quot;>{{#negSymbol}}&lt;span class=&quot;lf-minus-sign {{negSymbolClass}}&quot;>&amp;minus;&lt;/span>{{/negSymbol}}{{value}}&lt;/span>
                                        &lt;/td>
                                        {{/formattedValues}}
                                    &lt;/tr>
                                    {{/entries}}
                                    &lt;/tbody>
                                &lt;/table>
                                {{/.}}
                                {{/text}}
                            
                        

                    
                    Next Steps

                    
                        Enable color patterns
                            
                                Total cost, in today&quot; , &quot;'&quot; , &quot;s dollars, of Loan 1 and Loan 2Bar chart with 2 data series.View as data table, Total cost, in today&quot; , &quot;'&quot; , &quot;s dollars, of Loan 1 and Loan 2The chart has 1 X axis displaying categories. The chart has 1 Y axis displaying values. Data ranges from 5824 to 23985.Chart context menuTotal cost, in today&quot; , &quot;'&quot; , &quot;s dollars, of Loan 1 and Loan 2Loan 1Loan 2020k5k10k15k25k30kTotal CostLoan 2: $5,824End of interactive chart.
                            
                            
                                
                            
                            
                                
                                    
                                        Payment Schedule - Loan 1
                                        
                                            SEE TABLE
                                        
                                    
                                    
                                        Payment Schedule - Loan 2
                                        
                                            SEE TABLE
                                        
                                    
                                
                            
                        
                        Change views between Total Cost graph, Payments graph, and Amortization tables
                        
                            
                            
                                
                                Total Cost
                            
                            
                                
                                Payments
                            
                            
                                
                                Amortization
                            
                        
                    
                    
                        &lt;div class=&quot;lf-graph-list&quot;>
                            {{#.}}
                            {{^table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph{{#first}} lf-active{{/first}}&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div id=&quot;{{id}}&quot; class=&quot;lf-cm-90 lf-graph_container&quot;>&lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{#table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph lf-hidden&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div class=&quot;lf-tables-links&quot;>
                                    {{#tables}}
                                    &lt;div>
                                        {{#label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-multiple&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-multiple&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                        {{^label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-single&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-single&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                    &lt;/div>
                                    {{/tables}}
                                &lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{/.}}
                        &lt;/div>
                        &lt;div id=&quot;lf_selectors_description&quot; class=&quot;visually-hidden&quot;>&lt;/div>
                        &lt;div class=&quot;lf-results-loop&quot; role=&quot;tablist&quot; aria-labelledby=&quot;lf_selectors_description&quot;>
                            &lt;span class=&quot;lf-divider&quot;>&lt;/span>
                            {{#.}}
                            &lt;button id=&quot;lf_graph_tabs_{{id}}&quot; class=&quot;{{#first}}lf-active {{/first}}{{#last}}lf-last-loop{{/last}} lf-graph-tabs&quot; role=&quot;tab&quot; aria-selected=&quot;false&quot; aria-controls=&quot;{{id}}-graph-container&quot; tabindex=&quot;-1&quot;>
                                &lt;span class=&quot;lf-dot&quot; aria-hidden=&quot;true&quot;>&lt;/span>
                                &lt;div class=&quot;lf-dot-holder lf-dot-label&quot;>{{label}}&lt;/div>
                            &lt;/button>
                            {{/.}}
                        &lt;/div>
                    
                    
                        
                    
                &quot;) or . = concat(&quot;
                    Results
                    
                        Loan 2 will cost you, in today&quot; , &quot;'&quot; , &quot;s dollars, $18,161 less than Loan 1
                    
                    
                        Loan 2 will cost you, in today&quot; , &quot;'&quot; , &quot;s dollars, $18,161 less than Loan 1

                        Show details

                        

                            
                                Results details
                                    
                                    
                                        
                                        Loan 1
                                        Loan 2
                                    
                                    
                                    

                                    
                                        
                                            Monthly payment
                                        
                                        $875
                                        
                                        $570
                                        
                                    
                                    
                                        
                                            Total interest paid
                                        
                                        $4,005
                                        
                                        $688
                                        
                                    
                                    
                                
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;div class=&quot;lf-results-list lf-list-{{listNumber}}&quot;>
                                    {{#entries}}
                                    &lt;div id=&quot;lf_{{dataElementName}}_results&quot; class=&quot;lf-result-list-content&quot;>
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;lf-details-list-label&quot;>
                                            {{{fieldLabel}}}
                                        &lt;/div>
                                        {{#formattedValues}}
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_value&quot;
                                             class=&quot;lf-details-list-value&quot;>{{value}}
                                        &lt;/div>
                                        {{/formattedValues}}
                                    &lt;/div>
                                    {{/entries}}
                                &lt;/div>
                                {{/.}}
                                {{/text}}
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;tableCellADA&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;table class=&quot;lf-results-table&quot; id=&quot;lf-results-table-main&quot;>
                                    &lt;thead>
                                    &lt;tr>
                                        {{#columnHeaders}}
                                        &lt;td>&lt;/td>
                                        {{#columnHeader}}
                                        &lt;th class=&quot;lf-results-header tableCellADA&quot; scope=&quot;col&quot;>{{.}}&lt;/th>
                                        {{/columnHeader}}
                                        {{/columnHeaders}}
                                    &lt;/tr>
                                    &lt;/thead>
                                    &lt;tbody>
                                    {{#subtitle}}
                                    &lt;tr>
                                        &lt;th id=&quot;lf_result_subtitle1&quot;
                                            class=&quot;lf-result-subtitle tableCellADA three-headers&quot; colspan=&quot;100%&quot;>
                                            {{{subtitle}}}
                                        &lt;/th>
                                    &lt;/tr>
                                    {{/subtitle}}

                                    {{#entries}}
                                    &lt;tr id=&quot;lf_{{dataElementName}}_results&quot;>
                                        &lt;th id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;tableCellADA&quot; scope=&quot;row&quot;>
                                            {{#negLabel}}&lt;span class=&quot;lf-minus-sign&quot;>&amp;minus;&lt;/span>{{/negLabel}}{{{fieldLabel}}}
                                            {{#glossaryTerm}}
                                            &lt;a role=&quot;button&quot; href=&quot;#{{glossaryTerm}}&quot;
                                               id=&quot;lf_{{dataElementName}}_tooltip&quot;
                                               class=&quot;lf-show-tooltip lf_{{dataElementName}}_tooltip lf-more-details-tooltip&quot;
                                               {{#tooltipAriaLabel}}aria-label=&quot;{{{tooltipAriaLabel}}}&quot;{{/tooltipAriaLabel}}>
                                            &lt;div aria-hidden=&quot;true&quot; class=&quot;lf-tooltip-i lf-results-tooltip&quot;>i&lt;/div>
                                            &lt;/a>
                                            {{/glossaryTerm}}
                                        &lt;/th>
                                        {{#formattedValues}}
                                        &lt;td id=&quot;lf_{{dataElementName}}_result_value&quot;
                                            class=&quot;tableCellADA {{shownClasses}}&quot;>&lt;span class=&quot;lf-nbsp-number&quot;>{{#negSymbol}}&lt;span class=&quot;lf-minus-sign {{negSymbolClass}}&quot;>&amp;minus;&lt;/span>{{/negSymbol}}{{value}}&lt;/span>
                                        &lt;/td>
                                        {{/formattedValues}}
                                    &lt;/tr>
                                    {{/entries}}
                                    &lt;/tbody>
                                &lt;/table>
                                {{/.}}
                                {{/text}}
                            
                        

                    
                    Next Steps

                    
                        Enable color patterns
                            
                                Total cost, in today&quot; , &quot;'&quot; , &quot;s dollars, of Loan 1 and Loan 2Bar chart with 2 data series.View as data table, Total cost, in today&quot; , &quot;'&quot; , &quot;s dollars, of Loan 1 and Loan 2The chart has 1 X axis displaying categories. The chart has 1 Y axis displaying values. Data ranges from 5824 to 23985.Chart context menuTotal cost, in today&quot; , &quot;'&quot; , &quot;s dollars, of Loan 1 and Loan 2Loan 1Loan 2020k5k10k15k25k30kTotal CostLoan 2: $5,824End of interactive chart.
                            
                            
                                
                            
                            
                                
                                    
                                        Payment Schedule - Loan 1
                                        
                                            SEE TABLE
                                        
                                    
                                    
                                        Payment Schedule - Loan 2
                                        
                                            SEE TABLE
                                        
                                    
                                
                            
                        
                        Change views between Total Cost graph, Payments graph, and Amortization tables
                        
                            
                            
                                
                                Total Cost
                            
                            
                                
                                Payments
                            
                            
                                
                                Amortization
                            
                        
                    
                    
                        &lt;div class=&quot;lf-graph-list&quot;>
                            {{#.}}
                            {{^table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph{{#first}} lf-active{{/first}}&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div id=&quot;{{id}}&quot; class=&quot;lf-cm-90 lf-graph_container&quot;>&lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{#table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph lf-hidden&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div class=&quot;lf-tables-links&quot;>
                                    {{#tables}}
                                    &lt;div>
                                        {{#label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-multiple&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-multiple&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                        {{^label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-single&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-single&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                    &lt;/div>
                                    {{/tables}}
                                &lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{/.}}
                        &lt;/div>
                        &lt;div id=&quot;lf_selectors_description&quot; class=&quot;visually-hidden&quot;>&lt;/div>
                        &lt;div class=&quot;lf-results-loop&quot; role=&quot;tablist&quot; aria-labelledby=&quot;lf_selectors_description&quot;>
                            &lt;span class=&quot;lf-divider&quot;>&lt;/span>
                            {{#.}}
                            &lt;button id=&quot;lf_graph_tabs_{{id}}&quot; class=&quot;{{#first}}lf-active {{/first}}{{#last}}lf-last-loop{{/last}} lf-graph-tabs&quot; role=&quot;tab&quot; aria-selected=&quot;false&quot; aria-controls=&quot;{{id}}-graph-container&quot; tabindex=&quot;-1&quot;>
                                &lt;span class=&quot;lf-dot&quot; aria-hidden=&quot;true&quot;>&lt;/span>
                                &lt;div class=&quot;lf-dot-holder lf-dot-label&quot;>{{label}}&lt;/div>
                            &lt;/button>
                            {{/.}}
                        &lt;/div>
                    
                    
                        
                    
                &quot;))]</value>
      <webElementGuid>151741ee-47b9-4b5c-b1ce-853028e94a0a</webElementGuid>
   </webElementXpaths>
</WebElementEntity>
